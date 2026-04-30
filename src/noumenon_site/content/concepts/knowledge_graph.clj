(ns noumenon-site.content.concepts.knowledge-graph
  "Knowledge-graph concept page — three levels, plus schema overview."
  (:require [noumenon-site.render :as r]))

(def schema-rows
  [["Repository" "Repo, branch, working tree, head SHA"]
   ["Commit"     "SHA, author, timestamp, message, parent commits"]
   ["File"       "Path, size, language, line count, latest commit, summary"]
   ["Segment"    "Function/class/type with complexity, code smells, purity, calls"]
   ["Component"  "Logical subsystem with files, dependencies, and layer assignment"]
   ["Import"     "Edge between two files (resolved cross-file)"]
   ["Author"     "Email, display name, contribution history"]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "three-levels"} "Three levels"]
   [:p
    "Noumenon compiles a repository into a multi-level Datomic graph. "
    "A single query can join commit history, file structure, and component architecture."]
   [:div.layers-grid {:style "margin: 1.5rem 0 2rem;"}
    (r/card {:variant :green :tag "Micro" :title "Code segments"}
            [:p "Functions, classes, and types with complexity ratings, code smells, "
             "safety concerns, purity analysis, and call graphs. Extracted by an LLM "
             "during the analyze stage."])
    (r/card {:variant :purple :tag "Mid" :title "Files & imports"}
            [:p "Git history, authorship, change frequency, cross-file import edges, "
             "and per-file semantic summaries. Files connect upward to components "
             "and downward to segments."])
    (r/card {:variant :blue :tag "Macro" :title "Components & architecture"}
            [:p "Logical subsystems identified from directory structure, import "
             "graphs, and file summaries. Component dependencies, architectural "
             "layers, and category labels."])]

   [:h2 {:id "schema-overview"} "Schema overview"]
   [:p "The graph schema is intentionally small. A handful of entity types, "
    "each with a few canonical attributes."]
   [:table.md-table
    [:thead [:tr [:th "Entity"] [:th "Key facts"]]]
    (into [:tbody]
          (for [[entity facts] schema-rows]
            [:tr [:td [:code entity]] [:td facts]]))]
   [:p
    "Run " [:code "noum show-schema"] " or call "
    [:code "noumenon_get_schema"] " (MCP) to see the full attribute list."]

   [:h2 {:id "querying"} "Querying the graph"]
   [:p
    "All structure is queryable via Datalog. Noumenon ships with a "
    [:a {:href "/queries/"} "catalog of 90+ named queries"]
    " covering hotspots, dependency analysis, contributor graphs, and more. "
    "Pose natural-language questions with " [:code "noum ask"] ", or invoke "
    "queries directly from the CLI, HTTP API, or MCP."]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Knowledge graph"]
    [:p.lead
     "Noumenon compiles a repository into a multi-level Datomic graph. "
     "Queries traverse between levels naturally."]
    (prose-body)]])
