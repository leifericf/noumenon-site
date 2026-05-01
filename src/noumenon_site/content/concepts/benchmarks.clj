(ns noumenon-site.content.concepts.benchmarks
  "Benchmarks concept page: methodology plus the nine-repo result table."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def benchmark-rows
  ;; 2026-04-03 cross-repo run from the embed-tfidf branch with the
  ;; :embedded layer added alongside :raw and :full.
  ;; Columns: repo, language, raw, full, embedded, delta-vs-raw.
  [["ripgrep"  "Rust"       "41.9%" "75.0%" "47.2%" "+33.1pp"]
   ["ring"     "Clojure"    "52.6%" "75.7%" "45.9%" "+23.1pp"]
   ["flask"    "Python"     "44.9%" "65.4%" "46.1%" "+20.5pp"]
   ["fresh"    "TypeScript" "48.7%" "67.1%" "47.4%" "+18.4pp"]
   ["noumenon" "Clojure"    "44.7%" "56.8%" "35.1%" "+12.1pp"]
   ["guava"    "Java"       "43.1%" "51.5%" "22.2%"  "+8.4pp"]
   ["redis"    "C"          "38.2%" "46.2%" "39.7%"  "+8.0pp"]
   ["fzf"      "Go"         "52.7%" "56.9%" "48.6%"  "+4.2pp"]
   ["express"  "JavaScript" "62.2%" "63.5%" "50.0%"  "+1.3pp"]
   ["Average"  ""           "47.7%" "62.0%" "42.5%" "+14.3pp"]])

(defn- benchmark-row [[repo lang raw full embedded delta]]
  (let [last? (= repo "Average")]
    [:tr (when last? {:style "border-top: 1px solid currentColor;"})
     [:td (if last? [:strong repo] [:code repo])]
     [:td lang]
     [:td.num raw]
     [:td.num (if last? [:strong full] full)]
     [:td.num embedded]
     [:td.num delta]]))

(defn- stat-cards []
  [:div.layers-grid {:style "margin: 1rem 0 2.5rem;"}
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number (h/raw "1.3&times;")]
           [:h3 "More Accurate"]
           [:p "Without 47.7% → With 62.0% mean across 9 repos."])
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number (h/raw "3.7&times;")]
           [:h3 "Better Per Token"]
           [:p "TF-IDF-only context delivers more quality per input token than the full KG."])
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number "9 repos"]
           [:h3 "Seven Languages"]
           [:p "Same 40 questions per repo. Reproducible with " [:code "noum bench"] "."])])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "headline"} "Headline Numbers"]
   (stat-cards)
   [:p
    [:em "Run date 2026-04-03, "]
    [:em "embed-tfidf branch. Speed and per-question token figures from "]
    [:em "earlier runs are intentionally omitted; numbers below come "]
    [:em "from a single benchmark run on this date and method."]]

   [:h2 {:id "methodology"} "Methodology"]
   [:p
    "Nine repositories, forty hand-written questions per repo, three "
    "context conditions (" [:code ":raw"] ", " [:code ":full"] ", "
    [:code ":embedded"]
    "), one answerer model, one judge. The point of holding everything "
    "else still is to isolate the effect of the context, not the model "
    "or the agent loop."]

   [:h3 {:id "ask"} "How Each Question Is Asked"]
   [:p
    "Every condition uses the same answerer model with the same "
    "temperature and the same prompt template. The prompt is a single "
    "turn:"]
   [:pre [:code {:data-lang "bash"}
          "You are answering a question about a software codebase.\n\nContext (...):\n<context block>\n\nQuestion: <question>\n\nProvide a detailed, accurate answer based on the context provided."]]
   [:p
    [:strong "The model has no tools and no agent loop. "]
    "It cannot read files, run shell commands, call queries, follow up "
    "with another turn, or browse the codebase. The only thing it sees "
    "beyond the question is whatever sits in the context block. That's "
    "deliberate: the benchmark measures " [:em "context quality"]
    ", not agent behavior."]
   [:p
    "This is not " [:code "noum ask"] ". The interactive "
    [:a {:href "/concepts/ask/"} "Ask agent"]
    " in regular use has TF-IDF seeding, a routing-model hint, and "
    "iterative tool use. The benchmark freezes that surface so the "
    "comparison is apples-to-apples."]

   [:h3 {:id "without"} "What " [:code ":raw"] " Means"]
   [:p
    "The context block is the repo's source code. Files are listed via "
    [:code "git ls-tree HEAD"] ", read in order, and concatenated wrapped "
    "in " [:code "<file-content>"] " delimiters. Per-file content is "
    "capped at 10,000 characters. Total context is capped at the API "
    "budget; when the budget runs out, the remaining files are dropped "
    "from the tail."]
   [:p
    "This is more honest than \"empty context\" or \"only the README,\" "
    "but it is not the strongest baseline you could build. A developer "
    "using Claude Code or Cursor against the same repo would let the "
    "model call " [:code "read_file"] " and " [:code "grep"]
    " on demand instead of pasting everything up front. We have not "
    "benchmarked against that baseline. Take " [:code ":raw"]
    " as \"raw paste,\" not \"best you can do without Noumenon.\""]

   [:h3 {:id "with"} "What " [:code ":full"] " Means"]
   [:p
    "Each question carries a " [:code ":query-name"]
    " in the question file: the named Datalog query whose result "
    "contains the facts the question is about. For the " [:code ":full"]
    " run, that query executes against the knowledge graph, and the "
    "structured result becomes the context block."]
   [:p
    "The result is typically far smaller than the raw source dump and "
    "contains pre-extracted facts (file paths, complexity ratings, "
    "layer assignments, dependency edges, contributor counts, etc.) "
    "instead of code. The model still answers from context only, but "
    "the context is the answer-shaped data Noumenon's pipeline already "
    "pulled out."]

   [:h3 {:id "embedded"} "What " [:code ":embedded"] " Means"]
   [:p
    "A third condition added in the TF-IDF work. The context block is "
    "the top fifteen results from a TF-IDF cosine-similarity search of "
    "the question against per-file and per-component summaries: file "
    "paths plus their summaries plus a relevance score. No graph "
    "traversal. No Datalog. Just retrieval."]
   [:p
    "It runs much smaller than " [:code ":full"]
    " context-wise (typically thousands rather than tens of thousands "
    "of input tokens) and captures roughly three quarters of the "
    [:code ":full"]
    " mean accuracy on its own. By the per-question token measure, "
    [:code ":embedded"] " delivers about "
    [:strong "3.7× more quality per input token"]
    " than " [:code ":full"]
    ". This isn't a replacement for the graph; it's the cheapest tier "
    "for cost-bounded use, and it's what the production Ask agent "
    "warms up with before a single Datalog query runs. See "
    [:a {:href "/concepts/ask/"} "Ask"]
    " for how the two compose at runtime."]

   [:h3 {:id "questions"} "Question Set"]
   [:p
    "Forty questions, hand-written, covering three categories. The "
    "same set runs against every repo. Concrete examples from the "
    "current "
    [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/benchmark/questions.edn"}
     "questions.edn"] ":"]
   [:ul
    [:li
     [:strong "Single-hop (deterministic). "]
     "Answerable from one query result. "
     [:em "\"Which source files are rated as 'complex' or 'very-complex'?\""]
     " — \" Who are the top three contributors by commit count?\""]
    [:li
     [:strong "Multi-hop (LLM-judged). "]
     "Combine multiple facts. "
     [:em "\"Which files are most frequently changed together? What does this suggest about coupling?\""]
     " — \"Which files are classified as 'trivial' complexity AND in the 'core' architectural layer?\""]
    [:li
     [:strong "Architectural (LLM-judged). "]
     "Synthesis and reasoning. "
     [:em "\"Describe the overall architecture in terms of its layers and how they relate.\""]
     " — \"Based on the file complexity distribution, where would you focus a code review? Why?\""]]

   [:h3 {:id "scoring"} "How Answers Are Scored"]
   [:p
    "Two scoring paths. Single-hop questions score "
    [:strong "deterministically"]
    ": the same Datalog query that produced the context (or, in the "
    [:code ":raw"]
    " case, would have) is the ground-truth. The scorer pulls the "
    "expected file paths, layer keywords, or contributor names out of "
    "that result and checks (with word-boundary regex) whether the "
    "answer mentions them. " [:code ":correct"] ", " [:code ":partial"]
    ", or " [:code ":wrong"] " is purely mechanical."]
   [:p
    "Multi-hop and architectural questions go through an "
    [:strong "LLM judge"]
    " using a fixed rubric template that ships with the benchmark. The "
    "judge sees only the question, the per-question rubric, and the "
    "answer text. It does " [:em "not"]
    " see the source code, the knowledge graph, or which condition "
    "produced the answer. The rubric includes calibration examples to "
    "anchor the score scale."]

   [:h3 {:id "selection"} "Repo and Question Selection"]
   [:p
    "Nine repositories chosen for language coverage, not for outcomes:"]
   [:ul
    [:li [:code "ring"] " (Clojure), " [:code "flask"] " (Python), "
     [:code "express"] " (JavaScript), " [:code "fresh"] " (TypeScript)."]
    [:li [:code "ripgrep"] " (Rust), " [:code "fzf"] " (Go), "
     [:code "redis"] " (C), " [:code "guava"] " (Java) from the repo "
     "manifest at "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/benchmark/repos.edn"}
      "resources/benchmark/repos.edn"] "."]
    [:li
     "The " [:code "noumenon"] " repo runs against itself as a sanity "
     "check, and is included in the table below."]]
   [:p
    "Question selection followed dev-relevance, not where Noumenon "
    "happens to be strong. The set has been kept stable so the numbers "
    "are comparable across releases. The " [:code "redis"]
    " and " [:code "express"]
    " rows are illustrative: large codebases the LLM already knows "
    "well from training data, where Noumenon's lift is smaller."]

   [:h3 {:id "counted"} "What's Counted"]
   [:ul
    [:li
     [:strong "Accuracy. "]
     "Mean score across all 40 questions per repo, weighted equally "
     "between deterministic and LLM-judged."]
    [:li
     [:strong "Layers. "] "Three context conditions per question: "
     [:code ":raw"] ", " [:code ":full"] ", " [:code ":embedded"]
     ". Per-layer means feed the headline numbers; "
     [:code ":embedded"]
     " also enables the cost-efficiency comparison."]]
   [:p
    "Speed and per-question token figures from earlier reports are not "
    "represented here. We dropped them rather than carry forward "
    "numbers we cannot vouch for under the current harness."]

   [:h3 {:id "flaws"} "Known Flaws and Limitations"]
   [:p
    "The point of this section is to be specific, not to wave a hand "
    "at uncertainty. Anyone reading the headline numbers should also "
    "read this list."]
   [:ul
    [:li
     [:strong "The " [:code ":full"] " condition is given a pre-computed answer. "]
     "Every question carries a " [:code ":query-name"]
     " whose result is, by design, close to the answer. The "
     [:code ":full"]
     " model is mostly summarizing a structured result. The "
     [:code ":raw"]
     " model is asked to find the same fact inside tens of thousands "
     "of characters of source. That is a real design choice in "
     "Noumenon's favor, not just a context-size difference."]
    [:li
     [:strong "The question set was written to match existing queries. "]
     "We have not benchmarked anything Noumenon doesn't already have a "
     "named query for. Coverage is biased toward questions the "
     "pipeline produces clean context for. Harder or less-mappable "
     "questions are absent because they didn't get written, not "
     "because the pipeline answers them well."]
    [:li
     [:strong "Single run per repo, no variance bars. "]
     "Run-to-run variance from API sampling, judge nondeterminism, and "
     "concurrent extraction order is not quantified. A repeat of any "
     "row would land at a slightly different number. " [:code ":full"]
     " is reliably ahead of " [:code ":raw"]
     ", but the per-repo gap should not be read past one significant "
     "figure."]
    [:li
     [:strong "The judge is an LLM. "]
     "LLM-as-judge has well-documented biases: agreement with itself, "
     "preference for fluent or verbose answers, drift across runs. The "
     "rubric ships with calibration examples but does not eliminate "
     "these. We do not currently have a human-graded sample to anchor "
     "against."]
    [:li
     [:strong "The " [:code ":raw"] " truncation can drop the relevant file. "]
     "When the raw source exceeds the API budget, the tail of "
     [:code "git ls-tree"] " is dropped. If the file containing the "
     "answer is late in that order, the " [:code ":raw"]
     " run is being scored on a context that genuinely cannot answer "
     "the question. We do not currently report what fraction of "
     "questions are affected per repo."]
    [:li
     [:strong "The " [:code "noumenon"] " repo is in the test set. "]
     "We benchmark our own codebase as one of the nine repos. The "
     "pipeline has been iterated on with that data in the loop, so "
     "that row is double-dipping. We keep it because the trend across "
     "the other eight repos is what matters, but the "
     [:code "noumenon"] " row is not a blind result."]
    [:li
     [:strong "Cost numbers exclude the upfront analyze pass. "]
     "When we report a per-question token number for the "
     [:code ":full"] " or " [:code ":embedded"]
     " condition, that's only the answerer's input. It does not "
     "include the LLM tokens spent during analyze and synthesize to "
     "build the graph. For a one-off question on a fresh repo, "
     [:code ":raw"]
     " is cheaper. The break-even depends on how many questions you "
     "ask per repo before the upfront cost amortizes."]
    [:li
     [:strong "The " [:code ":embedded"] " advantage is partly trivial. "]
     "TF-IDF context is much smaller than full-graph context, so per-"
     "token efficiency is partly a consequence of context size, not "
     "Noumenon being clever. The interesting result is that small "
     "context still captures roughly three quarters of the "
     [:code ":full"] " mean."]
    [:li
     [:strong "Deterministic scoring is regex-based. "]
     "Single-hop scoring checks for file paths or layer keywords "
     "appearing in the answer text. A correct answer that paraphrases "
     "or uses a synonym scores wrong. This biases against the "
     [:code ":raw"] " condition less than the " [:code ":full"]
     " condition (raw-source answers tend to quote filenames "
     "verbatim) but the noise is real either way."]
    [:li
     [:strong "Garden-path risk in authorship. "]
     "Both the question set and the named queries were written by the "
     "same person who wrote Noumenon. Questions probing aspects of "
     "code understanding the pipeline doesn't do well are likely "
     "under-represented just because they didn't get written. An "
     "external question set would be more telling and we don't have "
     "one."]
    [:li
     [:strong "One model, one temperature. "]
     "Numbers are from a single answerer model. The lift may shrink "
     "with a larger frontier model that needs less context-shaping "
     "help, or grow with a smaller one. We have not swept the model "
     "axis."]
    [:li
     [:strong "Out of scope entirely: real agentic Ask. "]
     "The interactive Ask agent in production seeds with TF-IDF, calls "
     "tools, and refines across turns. The benchmark deliberately "
     "holds that loop still so the comparison is apples-to-apples on "
     "context. It is silent on whether agentic Ask is better or worse "
     "than single-turn KG context."]]
   [:p
    "We list these because the alternative is selling the result. The "
    "knowledge graph has real benefits that survive the caveats: "
    "structured facts compose, queries are reproducible, and "
    "per-question cost is predictable once the graph exists. Treat "
    "the headline numbers as directional and reproduce locally on "
    "your own repos before quoting them."]

   [:h2 {:id "full-table"} "Per-Repository Results"]
   [:table.benchmark-table
    [:thead
     [:tr
      [:th "Repository"] [:th "Language"]
      [:th.num [:code ":raw"]]
      [:th.num [:code ":full"]]
      [:th.num [:code ":embedded"]]
      [:th.num "Δ vs raw"]]]
    (into [:tbody] (map benchmark-row benchmark-rows))]
   [:p {:style "font-size: 0.85em;"}
    [:em "Run date 2026-04-03, embed-tfidf branch. "]
    "Reproduce with "
    [:code "noum bench <repo>"] " on a digested database."]

   [:div.callout
    [:p
     "Run the benchmark yourself with " [:code "noum bench <repo>"]
     ", retrieve a past run via " [:code "noum results <run-id>"]
     ", or compare two runs with " [:code "noum compare <a> <b>"]
     ". The MCP equivalents are "
     [:code "noumenon_benchmark_run"] ", "
     [:code "noumenon_benchmark_results"] ", and "
     [:code "noumenon_benchmark_compare"]
     ". Results land in the same Datomic graph as everything else."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Benchmarks"]
    [:p.lead
     "We evaluate Noumenon by asking the same 40 questions about 9 repos "
     "in 7 languages, across three context conditions: raw source, the "
     "full knowledge graph, and TF-IDF retrieval alone. Same model, same "
     "prompt template, same judge. The numbers are real, but the design "
     "choices behind them favor Noumenon in specific ways. See "
     [:a {:href "#flaws"} "Known Flaws and Limitations"] " before quoting."]
    (prose-body)]])

