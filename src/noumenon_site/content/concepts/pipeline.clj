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
    :body  "Cross-file import and dependency edges resolved by parsing source code. Supports Clojure, Python, JS/TS, Rust, Java, C#, C/C++, Go, Elixir, and Erlang. No LLM calls — this is structural extraction, not interpretation."}
   {:title "Analyze" :slug "analyze"
    :note  "Micro / LLM" :note-class nil
    :body  "An LLM reads each file and extracts code segments — functions, classes, types — with complexity ratings, code smells, safety concerns, purity analysis, and architectural hints. Parallelized with configurable concurrency. The most expensive stage."}
   {:title "Synthesize" :slug "synthesize"
    :note  "Macro / LLM" :note-class :macro
    :body  "Queries the graph to identify logical components, classify files into architectural layers, and map component dependencies. Uses hierarchical map-reduce so it scales to repos with thousands of files."}
   {:title "Embed" :slug "embed"
    :note  "Deterministic" :note-class nil
    :body  "Builds a TF-IDF vector index from file and component summaries. Powers semantic search via noumenon_search, and seeds the Ask agent with relevant files before any query runs. No LLM calls."}])

(defn page []
  [:section
   [:div.container
    [:h1 "Pipeline"]
    [:p.lead
     "Five stages turn a git repository into a queryable knowledge graph. "
     "Each stage is idempotent — re-running it costs nothing if nothing changed."]
    [:div.pipeline {:style "margin: 2rem 0 3rem;"}
     (interpose [:span.pipeline-arrow (h/raw "&rarr;")]
                (for [{:keys [title note note-class slug]} stages]
                  (r/pipeline-step
                   {:title title :subtitle "" :note note :note-class note-class
                    :tip ""})))]
    (for [{:keys [title slug body]} stages]
      [:div
       [:h2 {:id slug} title]
       [:p body]])
    [:div.callout
     [:p
      "After embed, the graph is ready to query. Iterative use commands — "
      [:code "noum ask"] ", " [:code "noum query"] ", and the MCP server — "
      "read from the same Datomic database. " [:code "noum introspect"] " "
      "uses the graph to improve itself; see "
      [:a {:href "/concepts/introspect/"} "Introspect"] "."]]]])
