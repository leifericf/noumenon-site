(ns noumenon-site.content.concepts.benchmarks
  "Benchmarks concept page: methodology plus the eight-repo result table."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def benchmark-rows
  [["flask"    "Python"     "13" "68%"  "41K" "8K"   "14.5s" "7.1s"]
   ["ripgrep"  "Rust"       "17" "67%"  "44K" "6K"   "14.0s" "5.3s"]
   ["ring"     "Clojure"    "28" "60%"  "27K" "4K"   "12.2s" "5.3s"]
   ["fresh"    "TypeScript" "25" "55%"  "23K" "10K"  "12.7s" "5.2s"]
   ["noumenon" "Clojure"    "16" "48%"  "30K" "6K"   "10.9s" "5.2s"]
   ["fzf"      "Go"         "22" "47%"  "43K" "1K"   "15.4s" "5.1s"]
   ["express"  "JavaScript" "25" "47%"  "39K" "7K"   "12.7s" "5.2s"]
   ["redis"    "C"          "10" "31%"  "53K" "18K"  "16.6s" "10.5s"]])

(defn- benchmark-row [[repo lang acc-w acc-with tok-w tok-with spd-w spd-with]]
  [:tr
   [:td [:code repo]] [:td lang]
   [:td.num (str acc-w " ") (h/raw "&rarr;") " " [:strong acc-with]]
   [:td.num (str tok-w " ") (h/raw "&rarr;") " " [:strong tok-with]]
   [:td.num (str spd-w " ") (h/raw "&rarr;") " " [:strong spd-with]]])

(defn- stat-cards []
  [:div.layers-grid {:style "margin: 1rem 0 2.5rem;"}
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number (h/raw "2.7&times;")]
           [:h3 "More Accurate"]
           [:p "20% → 53% mean score"])
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number "55% faster"]
           [:h3 "Less Waiting"]
           [:p "13.6s → 6.1s per question"])
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number "80% cheaper"]
           [:h3 "Fewer Tokens"]
           [:p "37K → 7K input tokens"])])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "headline"} "Headline Numbers"]
   (stat-cards)

   [:h2 {:id "methodology"} "Methodology"]
   [:p
    "Eight repositories, forty hand-written questions per repo, two conditions "
    "(\"without Noumenon\" and \"with Noumenon\"), one answerer model, one "
    "judge. The point of holding everything else still is to isolate the "
    "effect of the context, not the model or the agent loop."]

   [:h3 {:id "ask"} "How Each Question Is Asked"]
   [:p
    "Both conditions use the same answerer model with the same temperature "
    "and the same prompt template. The prompt is a single turn:"]
   [:pre [:code {:data-lang "bash"}
          "You are answering a question about a software codebase.\n\nContext (...):\n<context block>\n\nQuestion: <question>\n\nProvide a detailed, accurate answer based on the context provided."]]
   [:p
    [:strong "The model has no tools and no agent loop. "]
    "It cannot read files, run shell commands, call queries, follow up with "
    "another turn, or browse the codebase. The only thing it sees beyond the "
    "question is whatever sits in the context block. That's deliberate: the "
    "benchmark measures " [:em "context quality"] ", not agent behavior."]
   [:p
    "This is not " [:code "noum ask"] ". The interactive Ask agent in regular "
    "use does have TF-IDF seeding and iterative tool use. The benchmark "
    "freezes that surface so the comparison is apples-to-apples."]

   [:h3 {:id "without"} "What \"Without\" Means"]
   [:p
    "The context block is the repo's source code. Files are listed via "
    [:code "git ls-tree HEAD"] ", read in order, and concatenated wrapped in "
    [:code "<file-content>"] " delimiters. Per-file content is capped at "
    "10,000 characters. Total context is capped at the API budget — when the "
    "budget runs out, the remaining files are dropped from the tail."]
   [:p
    "This is more honest than \"empty context\" or \"only the README,\" but "
    "it is not the strongest baseline you could build. A developer using "
    "Claude Code or Cursor against the same repo would let the model call "
    [:code "read_file"] " and " [:code "grep"]
    " on demand instead of pasting everything up front. We have not "
    "benchmarked against that baseline. Take the \"without\" condition as "
    "\"raw paste,\" not \"best you can do without Noumenon.\""]

   [:h3 {:id "with"} "What \"With\" Means"]
   [:p
    "Each question carries a " [:code ":query-name"]
    " in the question file — the named Datalog query whose result contains "
    "the facts the question is about. For the \"with\" run, that query "
    "executes against the knowledge graph, and the structured result becomes "
    "the context block."]
   [:p
    "The result is typically far smaller than the raw source dump and "
    "contains pre-extracted facts (file paths, complexity ratings, layer "
    "assignments, dependency edges, contributor counts, etc.) instead of "
    "code. The model still answers from context only — but the context is "
    "the answer-shaped data Noumenon's pipeline already pulled out."]

   [:h3 {:id "questions"} "Question Set"]
   [:p
    "40 questions, hand-written, covering three categories. The same set "
    "runs against every repo:"]
   [:ul
    [:li
     [:strong "Single-hop (deterministic). "]
     "Answerable from one query result. Examples: which files are rated "
     "complex, which authors top the contributor list, which files are in a "
     "given layer."]
    [:li
     [:strong "Multi-hop (LLM-judged). "]
     "Combine multiple facts. Examples: which files change together and what "
     "that says about coupling, which trivial-complexity files sit in the "
     "core layer."]
    [:li
     [:strong "Architectural (LLM-judged). "]
     "Synthesis and reasoning. Examples: describe the layers and their "
     "relationships, where would you focus a code review and why."]]
   [:p
    "The full list lives at "
    [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/benchmark/questions.edn"}
     "resources/benchmark/questions.edn"] "."]

   [:h3 {:id "scoring"} "How Answers Are Scored"]
   [:p
    "Two scoring paths. Single-hop questions score "
    [:strong "deterministically"] ": the same Datalog query that produced the "
    "context (or, in the \"without\" case, would have) is the ground-truth. "
    "The scorer pulls the expected file paths, layer keywords, or contributor "
    "names out of that result and checks (with word-boundary regex) whether "
    "the answer mentions them. " [:code ":correct"] ", " [:code ":partial"]
    ", or " [:code ":wrong"] " is purely mechanical."]
   [:p
    "Multi-hop and architectural questions go through an "
    [:strong "LLM judge"] " using a fixed rubric template that ships with "
    "the benchmark. The judge sees only the question, the per-question "
    "rubric, and the answer text. It does " [:em "not"]
    " see the source code, the knowledge graph, or which condition (with or "
    "without) produced the answer. The rubric includes calibration examples "
    "to anchor the score scale."]

   [:h3 {:id "selection"} "Repo and Question Selection"]
   [:p
    "Eight repositories chosen for language coverage, not for outcomes:"]
   [:ul
    [:li [:code "ring"] " (Clojure), " [:code "flask"] " (Python), "
     [:code "express"] " (JavaScript), " [:code "fresh"] " (TypeScript)."]
    [:li [:code "ripgrep"] " (Rust), " [:code "fzf"] " (Go), "
     [:code "redis"] " (C), " [:code "guava"] " (Java) — picked from the "
     "repo manifest at "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/benchmark/repos.edn"}
      "resources/benchmark/repos.edn"] "."]
    [:li
     "The " [:code "noumenon"] " repo runs against itself as a sanity check, "
     "and is included in the table below."]]
   [:p
    "Question selection followed dev-relevance, not where Noumenon happens to "
    "be strong. The set predates most of the result tuning and has been kept "
    "stable so the numbers are comparable across releases. The "
    [:code "redis"] " row is illustrative: a large C codebase where Noumenon "
    "still helps but by a smaller margin than the Python or Rust repos."]

   [:h3 {:id "cost-speed"} "What's Counted"]
   [:ul
    [:li
     [:strong "Accuracy. "]
     "Mean score across all 40 questions per repo, weighted equally between "
     "deterministic and LLM-judged."]
    [:li
     [:strong "Tokens. "]
     "Input tokens to the answerer model, summed over all 40 prompts. "
     "Output tokens and judge-side tokens are recorded separately and not "
     "shown in the headline numbers."]
    [:li
     [:strong "Speed. "]
     "Wall-clock per question, from prompt-sent to answer-received. Network "
     "and provider queueing are included; the judge pass is not."]]

   [:h3 {:id "flaws"} "Known Flaws and Limitations"]
   [:p
    "The point of this section is to be specific, not to wave a hand at "
    "uncertainty. Anyone reading the headline numbers should also read this "
    "list."]
   [:ul
    [:li
     [:strong "The \"with\" condition is given a pre-computed answer. "]
     "Every question carries a " [:code ":query-name"]
     " whose result is, by design, close to the answer. The \"with\" model "
     "is mostly summarizing a structured result. The \"without\" model is "
     "asked to find the same fact inside tens of thousands of characters "
     "of raw source. That is a real design choice in Noumenon's favor, not "
     "just a context-size difference."]
    [:li
     [:strong "The question set was written to match existing queries. "]
     "We have not benchmarked anything Noumenon doesn't already have a "
     "named query for. Coverage is biased toward questions the pipeline "
     "produces clean context for. Harder or less-mappable questions are "
     "absent because they didn't get written, not because the pipeline "
     "answers them well."]
    [:li
     [:strong "Single run per repo, no variance bars. "]
     "Run-to-run variance from API sampling, judge nondeterminism, and "
     "concurrent extraction order is not quantified. A repeat of any row "
     "would land at a slightly different number. \"With\" is reliably "
     "ahead, but the per-repo gap should not be read past one significant "
     "figure."]
    [:li
     [:strong "The judge is an LLM. "]
     "LLM-as-judge has well-documented biases: agreement with itself, "
     "preference for fluent or verbose answers, drift across runs. The "
     "rubric ships with calibration examples but does not eliminate these. "
     "We do not currently have a human-graded sample to anchor against."]
    [:li
     [:strong "The \"without\" truncation can drop the relevant file. "]
     "When the raw source exceeds the API budget, the tail of "
     [:code "git ls-tree"] " is dropped. If the file containing the answer "
     "is late in that order, the \"without\" run is being scored on a "
     "context that genuinely cannot answer the question. We do not "
     "currently report what fraction of questions are affected per repo."]
    [:li
     [:strong "The "] [:code "noumenon"]
     [:strong " repo is in the test set. "]
     "We benchmark our own codebase as one of the eight repos. The pipeline "
     "has been iterated on with that data in the loop, so that row is "
     "double-dipping. We keep it because the trend across the other seven "
     "repos is what matters, but the 48% figure is not a blind result."]
    [:li
     [:strong "Cost excludes the upfront analyze pass. "]
     "The \"80% cheaper\" number is per-question input tokens to the "
     "answerer. It does " [:em "not"] " include the LLM tokens spent during "
     "analyze and synthesize to build the graph. For a one-off question on "
     "a fresh repo, raw context is cheaper. The break-even depends on how "
     "many questions you ask per repo before the upfront cost amortizes."]
    [:li
     [:strong "Some of the speed gain is trivial. "]
     "A smaller context block ships faster and decodes faster. Part of the "
     "55% speed advantage is just \"fewer tokens to process,\" which is a "
     "consequence of context size, not Noumenon being clever. Provider TTFT "
     "and decode rates dominate."]
    [:li
     [:strong "Deterministic scoring is regex-based. "]
     "Single-hop scoring checks for file paths or layer keywords appearing "
     "in the answer text. A correct answer that paraphrases or uses a "
     "synonym scores wrong. This biases against the \"without\" condition "
     "less than the \"with\" condition (raw-source answers tend to quote "
     "filenames verbatim) but the noise is real either way."]
    [:li
     [:strong "Garden-path risk in authorship. "]
     "Both the question set and the named queries were written by the same "
     "person who wrote Noumenon. Questions probing aspects of code "
     "understanding the pipeline doesn't do well are likely under-represented "
     "just because they didn't get written. An external question set would "
     "be more telling and we don't have one."]
    [:li
     [:strong "One model, one temperature. "]
     "Numbers are from a single answerer model. The lift may shrink with a "
     "larger frontier model that needs less context-shaping help, or grow "
     "with a smaller one. We have not swept the model axis."]
    [:li
     [:strong "Out of scope entirely: real agentic " [:code "noum ask"] ". "]
     "The interactive Ask agent in production seeds with TF-IDF, calls "
     "tools, and refines across turns. The benchmark deliberately holds "
     "that loop still so the comparison is apples-to-apples on context. "
     "It is silent on whether agentic Ask is better or worse than "
     "single-turn KG context."]]
   [:p
    "We list these because the alternative is selling the result. The "
    "knowledge graph has real benefits that survive the caveats: structured "
    "facts compose, queries are reproducible, and per-question cost is "
    "predictable once the graph exists. Treat the headline numbers as "
    "directional and reproduce locally on your own repos before quoting "
    "them."]

   [:h2 {:id "full-table"} "Per-Repository Results"]
   [:table.benchmark-table
    [:thead
     [:tr
      [:th "Repository"] [:th "Language"]
      [:th.num "Accuracy (Without → With)"]
      [:th.num "Tokens (Without → With)"]
      [:th.num "Speed (Without → With)"]]]
    (into [:tbody] (map benchmark-row benchmark-rows))]

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
     "We evaluate Noumenon by asking the same 40 questions about 8 repos in "
     "7 languages, with and without the knowledge graph. \"Without\" is raw "
     "source files; \"with\" is Noumenon-mediated context. Same model, same "
     "prompt template, same judge. The numbers below are real, but the "
     "design choices behind them favor Noumenon in specific ways. See "
     [:a {:href "#flaws"} "Known Flaws and Limitations"] " before quoting."]
    (prose-body)]])
