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
   [:h2 {:id "three-levels"} "Three Levels"]
   [:p
    "Noumenon compiles a repository into a multi-level Datomic graph. "
    "A single query can join commit history, file structure, and component architecture."]
   [:div.layers-grid {:style "margin: 1.5rem 0 2rem;"}
    (r/card {:variant :green :tag "Micro" :title "Code Segments"}
            [:p "Functions, classes, and types with complexity ratings, code smells, "
             "safety concerns, purity analysis, and call graphs. Extracted by an LLM "
             "during the analyze stage."])
    (r/card {:variant :purple :tag "Mid" :title "Files and Imports"}
            [:p "Git history, authorship, change frequency, cross-file import edges, "
             "and per-file semantic summaries. Files connect upward to components "
             "and downward to segments."])
    (r/card {:variant :blue :tag "Macro" :title "Components and Architecture"}
            [:p "Logical subsystems identified from directory structure, import "
             "graphs, and file summaries. Component dependencies, architectural "
             "layers, and category labels."])]

   [:h2 {:id "schema-overview"} "Schema Overview"]
   [:p "The graph schema is intentionally small. A handful of entity types, "
    "each with a few canonical attributes."]
   [:table.md-table
    [:thead [:tr [:th "Entity"] [:th "Key facts"]]]
    (into [:tbody]
          (for [[entity facts] schema-rows]
            [:tr [:td [:code entity]] [:td facts]]))]
   [:p
    "The full picture, with every attribute and the entity diagram, lives at "
    [:a {:href "/schema/"} "Schema"] ". For the live schema as the daemon "
    "sees it, run " [:code "noum show-schema"] " or call "
    [:code "noumenon_get_schema"] " over MCP."]

   [:h2 {:id "querying"} "Querying the Graph"]
   [:p
    "All structure is queryable via Datalog. Noumenon ships with a "
    [:a {:href "/queries/"} "catalog of 90+ named queries"]
    " covering hotspots, dependency analysis, contributor graphs, and more. "
    "Pose natural-language questions with " [:code "noum ask"] ", or invoke "
    "queries directly from the CLI, HTTP API, or MCP."]

   [:h2 {:id "vs-rag"} "Compared to RAG"]
   [:p
    "Retrieval-Augmented Generation (RAG) systems use vector "
    "similarity to find relevant documents before the LLM sees them. "
    "Noumenon's knowledge graph is a different shape: it stores facts "
    "and relationships as queryable structure, not chunks and "
    "embeddings. The two are complementary, not opposed."]
   [:table.md-table
    [:thead [:tr [:th "Job"] [:th "RAG is good at"] [:th "Knowledge graph is good at"]]]
    [:tbody
     [:tr
      [:td "Discovery"]
      [:td "Fuzzy lexical/semantic match. \"Files about authentication\""]
      [:td "Bad without a hint; the agent has to guess where to look."]]
     [:tr
      [:td "Relationships"]
      [:td "Implicit, by chunk proximity. Often wrong on \"what depends on X.\""]
      [:td "Explicit. \"Which components depend on auth-system\" is a one-line query."]]
     [:tr
      [:td "Composition"]
      [:td "Hard. Combining \"about auth\" with \"changed in last 30 days\" requires reranking."]
      [:td "Datalog joins. Combine arbitrary facts with no extra machinery."]]
     [:tr
      [:td "Reproducibility"]
      [:td "Embeddings drift between models and quantizations."]
      [:td "Same query, same database state, same answer."]]]]
   [:p
    "Noumenon does both. The TF-IDF retrieval tier in the analyze "
    "pipeline gives the "
    [:a {:href "/concepts/ask/"} "Ask agent"]
    " a RAG-style warm start (\"where to look\"); the Datalog graph "
    "handles relationships and composition (\"how everything connects\")."]

   [:h2 {:id "branch-aware"} "Branch-Aware Graph"]
   [:p
    [:em "Experimental — interfaces may change between releases."] " "
    "Each database carries the branch it represents — " [:code ":branch/name"]
    ", " [:code ":branch/kind"] ", " [:code ":branch/vcs"]
    " — and the repo entity points to its current branch via "
    [:code ":repo/branch"]
    ". A hosted instance tracks " [:em "trunk"]
    "; each developer materializes a sparse " [:em "delta"] " database "
    "at " [:code "~/.noumenon/deltas/"]
    " for the local feature branch. Federated queries merge trunk and "
    "delta in one HTTP roundtrip so answers reflect the working branch. "
    "Files are also content-addressed via " [:code ":file/blob-sha"]
    " — analyses promote across blobs with matching prompt + model "
    "without re-invoking the LLM. See "
    [:a {:href "/concepts/source-control/#branches"} "Source control"]
    " for details."]

   [:h2 {:id "time-travel"} "Time Travel"]
   [:p
    "Datomic stores every transaction immutably and tags it with a "
    [:code "basis-t"]
    ". Old facts are not overwritten; they're superseded by newer "
    "facts at later transaction points. Any query can run \"as of\" "
    "any past basis."]
   [:p
    "This shows up two places: the HTTP API exposes "
    [:code "POST /api/query-as-of"]
    " with an ISO-8601 or epoch-ms parameter, and benchmark and "
    "introspect runs record the " [:code "basis-t"]
    " of the database they ran against. Reproducing a benchmark from "
    "six months ago means restoring the agent's prompt and "
    "re-querying the database " [:code "as-of"]
    " that same basis. The graph keeps its own history; we don't "
    "have to."]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Knowledge Graph"]
    [:p.lead
     "Noumenon compiles a repository into a multi-level Datomic graph. "
     "Queries traverse between levels naturally."]
    (prose-body)]])
