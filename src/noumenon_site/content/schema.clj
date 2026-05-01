(ns noumenon-site.content.schema
  "Schema reference page: Mermaid ER diagram of the core knowledge
   entities, plus the full attribute table parsed from the upstream
   schema EDN files."
  (:require [clojure.string :as str]
            [hiccup2.core :as h]
            [noumenon-site.parse.schema :as parse]))

(def er-diagram
  ;; Hand-written ER diagram of the core knowledge entities. Auth,
  ;; settings, ask sessions, benchmark/introspect runs, and artifact
  ;; tables are intentionally omitted from the picture so the
  ;; codebase-knowledge graph reads cleanly. The full attribute list
  ;; for every entity follows below.
  "erDiagram
    REPO {
        string repo_uri
        string repo_head_sha
    }
    COMMIT {
        string git_sha PK
        string commit_message
        long   commit_timestamp
        long   commit_additions
        long   commit_deletions
    }
    AUTHOR {
        string author_name
        string author_email
    }
    DIR {
        string dir_path PK
    }
    FILE {
        string file_path PK
        keyword file_lang
        long    file_lines
        long    file_size
    }
    CODE {
        string code_name
        keyword code_kind
        long    code_line_start
        long    code_line_end
        keyword code_complexity
    }
    COMPONENT {
        string component_name
        keyword arch_layer
    }
    CHUNK {
        long   chunk_index
        string chunk_text
    }
    REPO ||--o{ COMMIT : \"commits\"
    REPO ||--|| DIR : \"root\"
    DIR  ||--o{ DIR  : \"children\"
    DIR  ||--o{ FILE : \"contains\"
    COMMIT }o--|| AUTHOR : \"by\"
    COMMIT ||--o{ COMMIT : \"parents\"
    COMMIT }o--o{ FILE   : \"changed-files\"
    FILE }o--o{ FILE : \"imports\"
    FILE ||--o{ CODE : \"segments\"
    CODE }o--o{ CODE : \"calls\"
    COMPONENT }o--o{ FILE      : \"files\"
    COMPONENT }o--o{ COMPONENT : \"depends-on\"
    CHUNK }o--|| FILE      : \"of file\"
    CHUNK }o--|| COMPONENT : \"of component\"")

(def mermaid-init
  "import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
mermaid.initialize({
  startOnLoad: true,
  theme: 'dark',
  themeVariables: {
    darkMode: true,
    background: '#0d1117',
    primaryColor: '#161b22',
    primaryTextColor: '#e6edf3',
    primaryBorderColor: '#30363d',
    lineColor: '#58a6ff',
    secondaryColor: '#1f2937',
    tertiaryColor: '#0d1117'
  }
});")

(defn- ident-display [k]
  (if (namespace k) (str ":" (namespace k) "/" (name k)) (str k)))

(defn- attr-row [{:keys [ident type card unique doc]}]
  [:tr {:data-name (ident-display ident)
        :data-doc  (or doc "")}
   [:td [:code (ident-display ident)]]
   [:td (when type [:code type])]
   [:td (when card [:code card])]
   [:td (when unique "yes")]
   [:td doc]])

(defn- namespace-table [[ns attrs]]
  [:div
   [:h3 {:id (str "ns-" ns)} (str ":" ns "/")]
   [:table.schema-table
    [:thead
     [:tr [:th "Attribute"] [:th "Type"] [:th "Card."] [:th "Unique"] [:th "What it means"]]]
    (into [:tbody] (map attr-row attrs))]])

(defn- prose-body [attrs]
  (let [grouped    (parse/by-namespace attrs)
        total      (count attrs)
        ns-count   (count grouped)]
    [:div.prose
     [:h2 {:id "diagram"} "Entity Diagram"]
     [:p
      "How the codebase-knowledge entities connect. Auth, settings, ask "
      "sessions, benchmark/introspect runs, and the artifact tables sit "
      "in the same database but are intentionally omitted here so the "
      "graph reads cleanly. The full attribute list follows."]
     [:div.mermaid-frame
      [:pre.mermaid (h/raw er-diagram)]]

     [:h2 {:id "two-databases"} "Two Databases per Instance"]
     [:p
      "Every Noumenon instance hosts two kinds of database. "
      [:strong "Per-repo databases"]
      " hold facts derived from a specific repository: commits, files, "
      "code segments, components, the import graph, the TF-IDF index. "
      "One per imported repo. Identity is derived from the repo path or "
      "URL. Schema files: " [:code "core.edn"] ", "
      [:code "architecture.edn"] ", " [:code "synthesis.edn"]
      ", " [:code "provenance.edn"] "."]
     [:p
      "The " [:strong [:code "noumenon-internal"]]
      " meta database holds cross-cutting facts about Noumenon itself: "
      "introspect runs, ask sessions and feedback, named-query "
      "artifacts, prompts, auth tokens, settings. One per instance, "
      "fixed name. A prompt change affects every repo, so its history "
      "belongs in one place rather than fragmented per-repo. Schema "
      "files: " [:code "ask.edn"] ", " [:code "benchmark.edn"]
      ", " [:code "introspect.edn"] ", " [:code "artifacts.edn"]
      ", " [:code "auth.edn"] ", " [:code "settings.edn"] "."]
     [:p
      "Both run on the same Datomic Local storage and share the same "
      "schema-attribute namespace. The split is conceptual: "
      [:em "facts about your code"] " versus "
      [:em "facts about how Noumenon answered questions about your code"] "."]

     [:h2 {:id "traversal"} "Traversing in Both Directions"]
     [:p
      "Datomic refs are stored once but queried both ways. Forward, you name "
      "the attribute. Backward, you prefix the name with an underscore."]
     [:pre [:code {:data-lang "clojure"}
            ";; Forward: which files does this file import?\n[?file :file/imports ?dep]\n\n;; Backward: which files import this file?\n[?file :file/_imports ?dependent]\n\n;; Forward: which segments does this code segment call?\n[?caller :code/calls ?callee]\n\n;; Backward: which segments call this one?\n[?callee :code/_calls ?caller]"]]
     [:p
      "Every " [:code ":db.type/ref"]
      " in the schema below is a candidate for backward traversal. The Ask "
      "agent and the named-query catalog use both directions extensively."]

     [:h2 {:id "attributes"} "Attribute Reference"]
     [:p
      (str total " attributes across " ns-count " namespaces, parsed from ")
      [:a {:href "https://github.com/leifericf/noumenon/tree/main/resources/schema"}
       [:code "resources/schema/"]]
      " in the noumenon repo. Filter by name or by description text."]
     [:input.queries-filter
      {:id          "schema-filter"
       :type        "search"
       :placeholder "Filter attributes..."
       :aria-label  "Filter attributes"}]
     (for [[ns _ :as group] grouped
           :when (seq ns)]
       (namespace-table group))

     [:div.callout
      [:p
       "For the live schema as the running daemon sees it (including any "
       "attributes added since this site last built), run "
       [:code "noum show-schema <repo>"] " or call "
       [:code "noumenon_get_schema"] " over MCP."]]]))

(defn page []
  (let [attrs (parse/all)]
    [:section.docs
     [:div.container
      [:h1.docs-title "Schema"]
      [:p.lead
       "Noumenon stores everything it knows in one Datomic database per "
       "repo. The picture below is the entity graph; the table beneath it "
       "is every attribute, parsed from the upstream EDN files at build time."]
      (if (seq attrs)
        (prose-body attrs)
        [:p [:em "Schema source not available."]])
      [:script {:type "module"} (h/raw mermaid-init)]]]))
