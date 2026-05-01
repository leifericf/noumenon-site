(ns noumenon-site.content.concepts.ask
  "Ask agent concept page: the iterative loop, two-tier warm start,
   self-reflection, and the telemetry queries that surface what the
   agent does at runtime.")

(def ^:private telemetry-queries
  [["ask-empty-results"     "Datalog queries the agent ran that returned nothing."]
   ["ask-unanswered"        "Sessions where the agent exhausted its iteration budget without an answer."]
   ["ask-error-steps"       "Steps where the LLM emitted output the agent couldn't parse."]
   ["ask-popular-queries"   "Datalog patterns the agent writes most often. Candidates for new named queries."]
   ["ask-token-cost"        "Spend per session: input/output tokens and dollars."]
   ["ask-by-caller"         "Spend split by channel and caller (CLI, MCP, HTTP, agent vs human)."]
   ["ask-missing-attributes" "What the agent reports as missing from the data model."]
   ["ask-quality-issues"    "Data-quality problems the agent observed."]
   ["ask-suggested-queries" "Named queries the agent thinks should exist."]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "loop"} "The Loop"]
   [:p
    [:code "noum ask <repo> \"<question>\""]
    " runs an iterative agent against the knowledge graph. The agent "
    "alternates between proposing the next move and the daemon "
    "executing it. Five tools are available per turn:"]
   [:ul
    [:li [:code ":query"] " run a named or raw Datalog query."]
    [:li [:code ":schema"] " inspect attributes for a namespace."]
    [:li [:code ":rules"] " expand a derived rule used in queries."]
    [:li [:code ":reflect"] " annotate this session with what's missing or broken."]
    [:li [:code ":answer"] " emit the final answer and stop."]]
   [:p
    "Each turn picks one tool. The loop is bounded by an iteration "
    "budget (default 10). Every step is recorded on the session "
    "entity in the meta database, so the full reasoning trace is "
    "queryable after the fact."]

   [:h2 {:id "warm-start"} "Two-Tier Warm Start"]
   [:p
    "Before the first iteration, the agent receives two hints. They "
    "answer different questions:"]
   [:ul
    [:li
     [:strong "vector-seed (where to look). "]
     "A TF-IDF cosine-similarity search of the question against per-"
     "file and per-component summaries. The top fifteen results land "
     "in the system message as relevant entities. Built at "
     [:code "noum embed"] " time and cached on disk as a Nippy file "
     "at " [:code "<db-dir>/<db-name>/tfidf-index.nippy"] "."]
    [:li
     [:strong "model-hint (which queries to try). "]
     "A small feed-forward neural net trained on past benchmark and "
     "session data emits the top three Datalog patterns to try first. "
     "Pure-Clojure inference, runs in microseconds, zero token cost. "
     "Configured by " [:code "resources/model/config.edn"]
     " and retrained by the introspect " [:code ":train"]
     " target. The agent is free to ignore the suggestion; when no "
     "model is available the hint is omitted and behavior is unchanged."]]
   [:p
    "Together the two hints give the agent both " [:em "scope"]
    " (which entities matter) and " [:em "method"]
    " (which queries to start with) before any LLM call runs. In "
    "benchmarks the TF-IDF tier alone captures roughly three quarters "
    "of the full-KG mean accuracy; combined with the agent loop, the "
    "Ask agent ships with that lift built in. See "
    [:a {:href "/concepts/benchmarks/#embedded"} "the embedded layer"]
    " for the comparison."]

   [:h2 {:id "search"} "When You Don't Want the Full Loop"]
   [:p
    "The TF-IDF index is also exposed directly as "
    [:code "noumenon_search"] " (MCP) and "
    [:code "noum search"] " (CLI). It returns ranked file and "
    "component matches in milliseconds with zero LLM calls. Use it "
    "when you want \"which files are about X\" without paying for an "
    "Ask session."]

   [:h2 {:id "reflect"} "Agent Self-Reflection"]
   [:p
    "When the agent answers, it can also emit a "
    [:code ":reflect"] " step. This is structured feedback the "
    "system uses to improve itself:"]
   [:pre [:code {:data-lang "clojure"}
          "{:tool :reflect\n :args {:missing-attributes  [\"function-level dependency graph\"\n                       \"test-to-source file mapping\"]\n        :quality-issues     [\"some commit messages are empty\"\n                       \"author emails inconsistent across repos\"]\n        :suggested-queries  [\"files by cyclomatic complexity\"\n                       \"test coverage per source file\"]\n        :notes \"The schema has file-level analysis but no\n               function-level granularity.\"}}"]]
   [:p
    "Each field is persisted on the session entity in the meta "
    "database (" [:code ":ask.session/missing-attributes"] ", "
    [:code ":ask.session/quality-issues"] ", "
    [:code ":ask.session/suggested-queries"] ", "
    [:code ":ask.session/agent-notes"]
    "). Aggregated by frequency across all sessions, these fields "
    "feed the introspect optimizer's "
    [:code "{{ask-insights}}"]
    " block; see " [:a {:href "/concepts/introspect/#signals"}
                    "Signals from Ask Sessions"]
    " for the full loop."]

   [:h2 {:id "telemetry"} "Telemetry Queries"]
   [:p
    "The session store is queryable. Every named query below ships "
    "in the catalog and runs against the meta database via "
    [:code "noum query"] ", " [:code "noumenon_query"]
    " (MCP), or the HTTP API."]
   [:table.md-table
    [:thead [:tr [:th "Query"] [:th "Surfaces"]]]
    (into [:tbody]
          (for [[name desc] telemetry-queries]
            [:tr [:td [:code name]] [:td desc]]))]
   [:p
    "Browse the full catalog at "
    [:a {:href "/queries/"} "/queries/"] "."]

   [:div.callout
    [:p
     "The agent improves the system by being watched. Every session "
     "is data; the introspect loop reads that data; the next session "
     "answers a little better. The price is that real questions get "
     "stored verbatim in the meta database — see "
     [:a {:href "/concepts/data-safety/"} "Data Safety"]
     " for what that does and doesn't include."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Ask"]
    [:p.lead
     "The Ask agent answers questions about a repository by iterating "
     "over the knowledge graph. It starts warm: a TF-IDF retrieval and "
     "a routing-model hint pre-load scope and method before the first "
     "LLM call. Every step is recorded so the system can improve itself "
     "from real usage."]
    (prose-body)]])
