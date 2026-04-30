(ns noumenon-site.content.landing
  "Landing page sections — faithful Hiccup port of the original index.html.
   Phase 3 will rework IA. For now this matches the legacy design 1:1
   (minus the duplicated server-mode block, which is consolidated)."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

;; --- Section helpers ---

(defn- section
  "Outer <section> with optional id/alt-bg and a centered .container."
  [{:keys [id alt centered]} & body]
  (cond-> [:section]
    id       (conj {:id id})
    alt      (update-in [1] assoc :class "section-alt")
    true     (conj (into [:div {:class (cond-> "container"
                                         centered (str " section-center"))}]
                         body))))

(defn- divider [] [:hr.divider])

;; --- Hero ---

(def hero-terminal-body
  [[:span.prompt "$"] " noum ask ./my-repo "
   [:span.green "\"Which files are the biggest risk hotspots?\""]
   "\n\n"
   [:span.comment "# iteration 1 — querying files-by-complexity (47 results)"] "\n"
   [:span.comment "# iteration 2 — querying commit frequency and authorship"] "\n"
   [:span.comment "# iteration 3 — cross-referencing co-change patterns"] "\n"
   [:span.comment "# done — 3 iterations, 4 Datalog queries"]
   "\n\n"
   [:span.highlight "Risk hotspots"] " (high churn + high complexity + few contributors):\n\n"
   "  " [:span.highlight "file                        complexity     bus-factor  changes"] "\n"
   "  " [:span.purple    "src/core/parser.ts"]    "          very-complex   1           47\n"
   "  " [:span.purple    "src/api/middleware.ts"] "       complex        2           38\n"
   "  " [:span.purple    "src/db/migrations.ts"]  "        complex        1           31"])

(defn- hero []
  [:section.hero
   [:div.container
    [:div.hero-badge
     [:span (h/raw "&#9679;")]
     " Open source · Self-contained · MIT License"]
    [:h1 (h/raw "Precise, grounded answers<br>about your codebase.")]
    [:p.hero-sub
     "Compile your repository into a "
     [:a {:href "https://www.datomic.com"} "Datomic"]
     " knowledge graph. AI agents query structured facts instead of scanning raw source — "
     [:strong "2.7× more accurate"] ", "
     [:strong "80% cheaper"] ", "
     [:strong "55% faster"] " across 8 repos."]
    [:div.hero-actions
     [:a.btn.btn-primary   {:href "#get-started"} "Get Started"]
     [:a.btn.btn-secondary {:href "https://github.com/leifericf/noumenon"} "View on GitHub"]]
    (apply r/terminal hero-terminal-body)]])

;; --- Knowledge levels ---

(defn- knowledge-levels []
  (section {:id "layers" :alt true :centered true}
           [:h2.section-title "Three levels of understanding"]
           [:p.section-sub "A multi-level graph. Queries traverse between levels naturally."]
           [:div.layers-grid
            (r/card {:variant :green :tag "Micro" :title "Code Segments"}
                    [:p "Functions, classes, and types with complexity ratings, code smells, safety concerns, purity analysis, and call graphs."])
            (r/card {:variant :purple :tag "Mid" :title "Files & Imports"}
                    [:p "Git history, authorship, change frequency, cross-file import edges, and per-file semantic summaries."])
            (r/card {:variant :blue :tag "Macro" :title "Components & Architecture"}
                    [:p "Logical subsystems identified from directory structure, import graphs, and file summaries. Component dependencies, layers, and categories."])]))

;; --- Pipeline ---

(def build-steps
  [{:title "Import"     :subtitle "Git history & files"  :note "deterministic"
    :tip   "Commits, files, authors, diffs, and directories parsed from git history into Datomic. No LLM calls. Fully reproducible."}
   {:title "Enrich"     :subtitle "Import graph"         :note "deterministic"
    :tip   "Cross-file import/dependency edges resolved by parsing source code. Supports 10+ languages: Clojure, Python, JS/TS, Rust, Java, C#, C/C++, Go, Elixir, Erlang. No LLM calls."}
   {:title "Analyze"    :subtitle "Per-file semantics"   :note "micro / LLM"
    :tip   "An LLM reads each file and extracts code segments with complexity ratings, code smells, safety concerns, purity analysis, and architectural hints. Parallel processing with configurable concurrency."}
   {:title "Synthesize" :subtitle "Components & arch"    :note "macro / LLM" :note-class :macro
    :tip   "Queries the knowledge graph to identify logical components, classify files into architectural layers, and map component dependencies. Uses hierarchical map-reduce for large repos (1000+ files)."}
   {:title "Embed"      :subtitle "Vector search index"  :note "deterministic"
    :tip   "Builds a TF-IDF vector index from file and component summaries. Enables semantic search (noumenon_search) and seeds the Ask agent with relevant files before querying. No LLM calls."}])

(def use-steps
  [{:title "Query / Ask / Serve" :subtitle "Use the graph" :note "iterative"
    :tip   "65+ named Datalog queries, natural-language Ask agent with TF-IDF seeding, and MCP server for Claude Desktop/Code. Query the graph via CLI, HTTP API, or MCP tools."}
   {:title "Introspect"          :subtitle "Self-improve"  :note "autonomous" :note-class :autonomous
    :tip   "Autonomous loop: propose a change to prompts, examples, rules, or code, benchmark it, keep if improved, revert if not. Capped by iterations, hours, or cost."}])

(defn- pipeline-row [steps]
  (into [:div.pipeline]
        (interpose [:span.pipeline-arrow (h/raw "&rarr;")]
                   (map r/pipeline-step steps))))

(defn- how-it-works []
  (section {:id "how-it-works" :centered true}
           [:h2.section-title "How it works"]
           [:p.section-sub "Five stages, each building on the last."]
           [:p.eyebrow "Build"]
           (pipeline-row build-steps)
           [:p.eyebrow {:style "margin-top: 1.25rem;"} "Use"]
           (pipeline-row use-steps)))

;; --- Introspect ---

(def introspect-terminal-body
  [[:span.prompt "$"] " noum introspect ./my-repo --max-iterations 5 --provider glm\n\n"
   [:span.comment "# baseline mean=52.3% (22 deterministic questions)"] "\n"
   [:span.comment "# === Iteration 1/5 ==="] "\n"
   [:span.comment "# target=system-prompt — \"Fix empty result handling\""] "\n"
   [:span.comment "# IMPROVED +6.8% (52.3% -> 59.1%)"] "\n"
   [:span.comment "# === Iteration 2/5 ==="] "\n"
   [:span.comment "# target=examples — \"Add dependency query patterns\""] "\n"
   [:span.comment "# reverted (delta=-4.5%)"] "\n"
   [:span.comment "# === Iteration 3/5 ==="] "\n"
   [:span.comment "# target=examples — \"Replace low-impact examples\""] "\n"
   [:span.comment "# IMPROVED +11.4% (56.8% -> 68.2%)"] "\n\n"
   "Introspect complete: 2 improvements in 3 iterations (final score: 68.2%)"])

(defn- introspect []
  (section {:id "introspect" :centered true}
           [:h2.section-title "Self-improving introspection"]
           [:p.section-sub "Propose a change, benchmark it, keep or revert. Five targets: prompts, examples, rules, source code, and model config."]
           [:div {:style "margin-top: 2.5rem;"}
            (apply r/terminal introspect-terminal-body)]))

;; --- Benchmarks ---

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
   [:td repo] [:td lang]
   [:td.num (str acc-w " ") (h/raw "&rarr;") " " [:strong acc-with]]
   [:td.num (str tok-w " ") (h/raw "&rarr;") " " [:strong tok-with]]
   [:td.num (str spd-w " ") (h/raw "&rarr;") " " [:strong spd-with]]])

(defn- benchmarks []
  (section {:id "benchmarks" :alt true :centered true}
           [:h2.section-title "Measured on real codebases"]
           [:p.section-sub
            "40 questions per repo, 8 repos, 7 languages."
            [:br]
            [:strong "Without"] " = source files only. " [:strong "With"] " = Noumenon."]
           [:div.layers-grid {:style "margin-bottom: 2.5rem;"}
            (r/card {:variant :green :class "benchmark-stat"}
                    [:span.stat-number (h/raw "2.7&times;")]
                    [:h3 "More accurate"]
                    [:p "20% → 53% mean score"])
            (r/card {:variant :green :class "benchmark-stat"}
                    [:span.stat-number "55% faster"]
                    [:h3 "Less waiting"]
                    [:p "13.6s → 6.1s per question"])
            (r/card {:variant :green :class "benchmark-stat"}
                    [:span.stat-number "80% cheaper"]
                    [:h3 "Fewer tokens"]
                    [:p "37K → 7K input tokens"])]
           [:table.benchmark-table
            [:thead
             [:tr
              [:th "Repository"] [:th "Language"]
              [:th.num "Accuracy (Without → With)"]
              [:th.num "Tokens (Without → With)"]
              [:th.num "Speed (Without → With)"]]]
            (into [:tbody] (map benchmark-row benchmark-rows))]))

;; --- Features ---

(def feature-cards
  [["MCP server"
    "Full knowledge graph as MCP tools. Works with Claude Desktop, Claude Code, or any MCP client."]
   ["10+ languages"
    "Import graphs for Clojure, Python, JS/TS, Rust, Java, C#, C/C++, Go, Elixir, and Erlang."]
   ["Visual desktop UI"
    [:p "Force-directed graph, three-level drill-down, floating Ask overlay. " [:code "noum open"]]]
   ["Sensitive file protection"
    [:p [:code ".env"] ", " [:code "*.pem"] ", credentials: tracked but never read or sent to LLMs."]]
   ["Cost transparency"
    "Token estimates before you start, per-file telemetry as it runs, totals when done."]
   ["Incremental sync"
    "Up to 20 parallel workers. After first import, only new commits are processed."]])

(defn- feature-card [[title body]]
  (r/card {:title title}
          (if (string? body) [:p body] body)))

(defn- features []
  (section {:id "features" :alt true :centered true}
           [:h2.section-title "Features"]
           (into [:div.features-grid] (map feature-card feature-cards))))

;; --- Server mode ---

(def server-terminal-body
  [[:span.prompt "$"] " docker compose up -d\n"
   [:span.comment "# Noumenon daemon listening on 0.0.0.0:7891"] "\n\n"
   [:span.prompt "$"] " noum connect https://noumenon.corp.example.com --token noum_abc...\n"
   [:span.green "Connected to noumenon.corp.example.com as 'com'."] "\n\n"
   [:span.prompt "$"] " noum ask ./my-local-repo "
   [:span.green "\"What are the risk hotspots?\""] "\n"
   [:span.comment "# queries the shared knowledge graph transparently"]])

(defn- server-mode []
  (section {:id "server-mode" :alt true :centered true}
           [:h2.section-title "Share one knowledge graph across your team"]
           [:p.section-sub
            "Run Noumenon as a shared service for up to 200 concurrent users. "
            "One Docker command to deploy, token-based access, and all tools work transparently against the remote instance."]
           [:div.problem-grid
            (r/card {:title "One-command deploy"}
                    [:p "Docker Compose with env-var config. No Clojure knowledge needed. Health checks, auto-restart, and named volumes included."])
            (r/card {:title "Role-based access"}
                    [:p "Admin tokens manage the server. Reader tokens for everyone else. The server manages its own git clones and refresh schedule."])
            (r/card {:title "Zero-config clients"}
                    [:p [:code "noum connect <url>"] " makes every CLI command and MCP tool route to the shared instance. Local paths are translated automatically."])]
           [:div {:style "margin-top: 2.5rem;"}
            (apply r/terminal server-terminal-body)]))

;; --- Query showcase ---

(def queries-terminal-body
  [[:span.prompt "$"] " noum queries\n\n"
   [:span.output "  hotspots                Complexity and change frequency\n"
    "  bug-hotspots            Files with high fix-commit ratio\n"
    "  co-changed-files        Files that change together\n"
    "  top-contributors        Most active authors per file\n"
    "  files-by-complexity     Ranked by semantic complexity\n"
    "  files-by-layer          Grouped by architectural layer\n"
    "  component-dependencies  Component coupling graph\n"
    "  dependency-hotspots     High fan-in import targets\n"
    "  pure-segments           Pure functions and their files\n"
    "  llm-cost-total          Aggregate analysis cost"]
   "\n\n"
   [:span.prompt "$"] " noum query hotspots ./my-repo\n\n"
   [:span.highlight "file                      changes  complexity"] "\n"
   [:span.output "src/core/parser.ts        47       very-complex\n"
    "src/api/middleware.ts     38       complex\n"
    "src/db/migrations.ts      31       complex\n"
    "src/auth/session.ts       28       complex"]])

(defn- queries-showcase []
  (section {:id "queries"}
           [:div.showcase
            [:div.showcase-text
             [:h2.section-title "65+ named queries"]
             [:p
              "Pre-built Datalog queries: hotspots, bug-prone files, contributor graphs, "
              "dependency analysis, component coupling, LLM cost tracking. Deterministic and composable."]]
            (apply r/terminal queries-terminal-body)]))

;; --- Get started ---

(defn- step [n title code hint]
  [:div.step
   [:div.step-number n]
   [:div.step-content
    [:h3 title]
    [:code code]
    (when hint [:p.step-hint hint])]])

(defn- get-started []
  (section {:id "get-started" :alt true :centered true}
           [:h2.section-title "Get started"]
           [:p.section-sub
            "One command to install. See the "
            [:a {:href "https://github.com/leifericf/noumenon#readme"} "README"]
            " for details."]
           [:div.steps
            (step 1 "Install"
                  "curl -sSL https://noumenon.leifericf.com/install | bash"
                  [:span "Also via "
                   [:a {:href "https://brew.sh"} "Homebrew"]
                   " or "
                   [:a {:href "https://scoop.sh"} "Scoop"]
                   " (Windows)"])
            (step 2 "Try the demo"
                  "noum demo"
                  "Pre-built knowledge graph. No LLM credentials needed.")
            (step 3 "Ask a question"
                  "noum ask noumenon \"Which files are the biggest risk hotspots?\""
                  nil)]
           [:p.section-sub {:style "margin-top: 1.5rem;"}
            "Or: " [:code "noum digest /path/to/your/repo"] " to build your own knowledge graph."]))

;; --- Compose ---

(defn landing-page []
  [:div
   (hero)
   (knowledge-levels)
   (divider)
   (how-it-works)
   (divider)
   (introspect)
   (divider)
   (benchmarks)
   (divider)
   (features)
   (divider)
   (server-mode)
   (divider)
   (queries-showcase)
   (divider)
   (get-started)])
