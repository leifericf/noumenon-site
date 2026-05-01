(ns noumenon-site.content.cli
  "CLI reference page covering the noum launcher commands."
  (:require [noumenon-site.render :as render]))

(def groups
  [{:heading "Pipeline"
    :id "pipeline"
    :items
    [["digest"     "Run the full pipeline (import, enrich, analyze, synthesize, embed) end-to-end."]
     ["import"     "Parse git history and file structure into Datomic. No LLM calls."]
     ["enrich"     "Resolve cross-file import and dependency edges. No LLM calls."]
     ["analyze"    "Run LLM semantic analysis on repo files. The expensive stage."]
     ["synthesize" "Identify components and architectural layers from analyzed data."]
     ["embed"      "Build the TF-IDF vector index for semantic search."]
     ["update"     "Sync the knowledge graph with the latest git state. Incremental."]
     ["watch"      "Watch a repo and auto-update on new commits. Long-running."]
     ["reseed"     "Reload prompts, queries, and rules from disk."]]}
   {:heading "Query and Ask"
    :id "query-ask"
    :items
    [["ask"        "Ask a natural-language question. Iteratively queries the graph."]
     ["query"      "Run a named or raw Datalog query."]
     ["queries"    "List the named-query catalog."]
     ["schema"     "Show the database schema."]
     ["status"     "Entity counts for a repository."]
     ["sessions"   "List or view past ask sessions."]
     ["feedback"   "Submit feedback on an ask session."]]}
   {:heading "Benchmarks and Introspection"
    :id "bench"
    :items
    [["bench"       "Run benchmark evaluation against a fixed question set."]
     ["results"     "Get benchmark results (latest run or by id)."]
     ["compare"     "Compare two benchmark runs by per-layer score deltas."]
     ["introspect"  "Run the autonomous self-improvement loop."]
     ["history"     "Show artifact change history (prompt/model drift)."]]}
   {:heading "MCP and Visual UI"
    :id "interfaces"
    :items
    [["setup"       "Configure MCP for Claude Desktop or Claude Code."]
     ["serve"       "Start the MCP server over stdin/stdout (for clients to spawn)."]
     ["open"        "Open the experimental Electron desktop UI. See /concepts/desktop-ui/."]]}
   {:heading "LLM and Provider Config"
    :id "providers"
    :items
    [["llm-providers" "List configured LLM providers and which is the default."]
     ["llm-models"    "List available models per provider."]
     ["settings"      "View or update settings."]]}
   {:heading "Daemon and Connections"
    :id "daemon"
    :items
    [["start"       "Start the local daemon."]
     ["stop"        "Stop the local daemon."]
     ["ping"        "Check daemon health."]
     ["connect"     "Connect to a remote Noumenon instance via URL plus token."]
     ["connections" "List configured connections."]
     ["disconnect"  "Remove a saved connection."]]}
   {:heading "Databases and Maintenance"
    :id "admin"
    :items
    [["databases"   "List all known databases."]
     ["delete"      "Delete a database (prompts for confirmation)."]
     ["demo"        "Download a pre-built demo database for instant querying."]
     ["upgrade"     "Update noumenon.jar (re-run installer to update noum itself)."]
     ["version"     "Show the launcher version."]
     ["help"        "Show help. Pass a subcommand for command-specific options."]]}])

(defn- group-table [items]
  [:table.md-table
   [:thead [:tr [:th "Command"] [:th "What it does"]]]
   (into [:tbody]
         (for [[cmd summary] items]
           [:tr
            [:td [:code (str "noum " cmd)]]
            [:td summary]]))])

(defn- code-block [lang body]
  [:pre [:code {:data-lang lang} body]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "interactive"} "Interactive Mode"]
   [:p
    "Run " [:code "noum"] " with no arguments for a menu-driven TUI. "
    "Commands are grouped by category, repositories and ask sessions come "
    "from live data, and there's nothing to memorize."]
   (code-block "bash" "noum")
   [:p
    "Tab through the menu, pick a command, and supply parameters from "
    "selectable lists. Useful when you're exploring; the same actions are "
    "available as direct flags for scripts."]

   [:h2 {:id "selectors"} "Pipeline Selectors"]
   [:p
    "Pipeline commands (" [:code "analyze"] ", " [:code "enrich"] ", "
    [:code "update"] ", " [:code "digest"] ") accept selectors to scope "
    "the work to a subset of the repo:"]
   [:ul
    [:li [:code "--path src/foo"] " — only files under that directory."]
    [:li [:code "--include \"src/**/*.clj\""] " — glob whitelist."]
    [:li [:code "--exclude \"**/*_test.clj\""] " — glob blacklist."]
    [:li [:code "--lang clojure"] " — restrict to a single language."]]
   (code-block "bash" "noum analyze ./my-repo --include \"src/**/*.clj\" --exclude \"**/*_test.clj\"")

   [:h2 {:id "drift"} "Drift Handling"]
   [:p
    "When you change a prompt template or switch LLM models, prior analysis "
    "results are still valid until you decide otherwise. Drift is advisory: "
    "Noumenon logs which files were analyzed with a different prompt or model, "
    "but only re-analyzes them when you ask. Pass "
    [:code "--reanalyze prompt-changed"] " or "
    [:code "--reanalyze model-changed"]
    " (or " [:code "stale"] ", or " [:code "all"] ") to refresh."]

   (for [{:keys [heading id items]} groups]
     [:div.list-section {:id id}
      [:h2 heading]
      (group-table items)
      (when (= id "query-ask")
        [:div.callout
         [:p
          "Spend is queryable. " [:code "noum query llm-cost-total <repo>"]
          " sums input tokens, output tokens, and dollars across every "
          "recorded LLM call; " [:code "llm-cost-by-model"]
          " and " [:code "llm-cost-by-file"]
          " break the same totals down. The same queries run through "
          [:code "noumenon_query"]
          " over MCP and the HTTP API. See "
          [:a {:href "/concepts/data-safety/#transparency"} "Cost Transparency"]
          "."]])])

   [:div.callout
    [:p
     "Every command takes " [:code "--help"] " for full flags and examples. "
     "The launcher source is in "
     [:a {:href "https://github.com/leifericf/noumenon/tree/main/launcher/src/noum"}
      "launcher/src/noum"]
     "; the underlying daemon entry points live in "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/src/noumenon/main.clj"}
      "src/noumenon/main.clj"]
     "."]]])

(defn- sidebar []
  (render/sidebar-nav
   [{:heading "On this page"
     :items [{:href "#interactive" :label "Interactive Mode"}
             {:href "#selectors"   :label "Pipeline Selectors"}
             {:href "#drift"       :label "Drift Handling"}]}
    {:heading "Commands"
     :items (for [{:keys [heading id items]} groups]
              {:href            (str "#" id)
               :data-section-id id
               :label           (str heading " · " (count items))})}]))

(defn page []
  [:section.docs
   [:div.container-wide
    [:h1.docs-title "CLI"]
    [:p.lead
     "The " [:code "noum"] " launcher is the primary way to drive Noumenon "
     "from a shell. It's the same surface as the HTTP API and MCP tools, "
     "exposed as familiar commands with tab-completion and an interactive "
     "menu when invoked with no arguments."]
    [:div.docs-layout
     (sidebar)
     [:div.docs-content (prose-body)]]]])
