(ns noumenon-site.content.concepts.pipeline
  "Pipeline concept page — five stages, full prose."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def stages
  [{:title "Import" :slug "import"
    :note  "Deterministic" :note-class nil
    :body  "Commits, files, authors, diffs, and directory structure parsed from git history into Datomic. No LLM calls. Fully reproducible: re-running on the same git state produces the same graph."}
   {:title "Enrich" :slug "enrich"
    :note  "Deterministic" :note-class nil
    :body  "Cross-file import and dependency edges resolved by parsing source code. Supports Clojure, Python, JS/TS, Rust, Java, C#, C/C++, Go, Elixir, and Erlang. No LLM calls. This is structural extraction, not interpretation."}
   {:title "Analyze" :slug "analyze"
    :note  "Micro / LLM" :note-class nil
    :body  "An LLM reads each file and extracts code segments (functions, classes, types) with complexity ratings, code smells, safety concerns, purity analysis, and architectural hints. Parallelized with configurable concurrency. The most expensive stage."}
   {:title "Synthesize" :slug "synthesize"
    :note  "Macro / LLM" :note-class :macro
    :body  "Queries the graph to identify logical components, classify files into architectural layers, and map component dependencies. Uses hierarchical map-reduce so it scales to repos with thousands of files."}
   {:title "Embed" :slug "embed"
    :note  "Deterministic" :note-class nil
    :body  "Builds a TF-IDF vector index from file and component summaries. Powers semantic search via noumenon_search, and seeds the Ask agent with relevant files before any query runs. No LLM calls."}])

(defn- diagram []
  [:div.pipeline {:style "margin: 1.5rem 0 2.5rem;"}
   (interpose [:span.pipeline-arrow (h/raw "&rarr;")]
              (for [{:keys [title note note-class]} stages]
                (r/pipeline-step
                 {:title title :subtitle "" :note note :note-class note-class
                  :tip ""})))])

(defn- prose-body []
  [:div.prose
   (for [{:keys [title slug body]} stages]
     [:div
      [:h2 {:id slug} title]
      [:p body]])
   [:h2 {:id "scoping"} "Scoping the Work"]
   [:p
    "Pipeline commands accept selectors so you can run a subset of the repo "
    "without re-doing everything. Useful for big monorepos and tight loops "
    "while you're tuning prompts."]
   [:ul
    [:li [:code "--path src/foo"] " limits to a directory."]
    [:li [:code "--include \"src/**/*.clj\""] " is a glob whitelist."]
    [:li [:code "--exclude \"**/*_test.clj\""] " is a glob blacklist."]
    [:li [:code "--lang clojure"] " restricts to one language."]]
   [:p
    "Selectors apply to " [:code "analyze"] ", " [:code "enrich"]
    ", " [:code "update"] ", and " [:code "digest"] "."]

   [:h2 {:id "promotion"} "Promotion (Content-Addressed Cache)"]
   [:p
    [:em "Experimental — interfaces may change between releases."] " "
    "Before " [:code "analyze"]
    " calls the LLM on a file, it checks the current database for a "
    "previously-analyzed file whose " [:code ":file/blob-sha"]
    " matched the same content under the same "
    [:code ":prov/prompt-hash"] " and " [:code ":prov/model-version"]
    ". On a hit, the donor's analysis attrs are copied onto the "
    "recipient with " [:code ":prov/promoted-from"]
    " lineage and " [:em "no LLM call is made"] ". The result map "
    "reports " [:code "files-analyzed"] " alongside "
    [:code "files-promoted"] " so the cache hit rate is visible."]
   [:p
    "Pass " [:code "--no-promote"]
    " to bypass the cache and always invoke the LLM. Cross-DB "
    "promotion (a delta promoting from trunk) records the donor's "
    "db-name in " [:code ":prov/promoted-from-db-name"]
    " — the foreign tx-id is meaningless in the recipient DB, so the "
    "ref attr is omitted and the db-name acts as the breadcrumb."]

   [:h2 {:id "drift"} "Prompt and Model Drift"]
   [:p
    "When you change a prompt template or switch LLM models, prior analysis "
    "results are still valid until you decide otherwise. Drift is advisory "
    "by default. Noumenon logs which files were analyzed with a different "
    "prompt or model; pass " [:code "--reanalyze prompt-changed"] ", "
    [:code "--reanalyze model-changed"] ", "
    [:code "--reanalyze stale"] ", or "
    [:code "--reanalyze all"]
    " to refresh."]

   [:div.callout
    [:p
     "After embed, the graph is ready to query. The iterative commands "
     [:code "noum ask"] ", " [:code "noum query"] ", and the MCP server all "
     "read from the same Datomic database. " [:code "noum introspect"] " "
     "uses the graph to improve itself; see "
     [:a {:href "/concepts/introspect/"} "Introspect"] "."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Pipeline"]
    [:p.lead
     "Five stages turn a git repository into a queryable knowledge graph. "
     "Each stage is idempotent: re-running it costs nothing if nothing changed."]
    (diagram)
    (prose-body)]])
