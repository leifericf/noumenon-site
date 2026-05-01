(ns noumenon-site.content.mcp
  "MCP setup walkthroughs + tool list."
  (:require [hiccup2.core :as h]))

(def desktop-config
  "{
  \"mcpServers\": {
    \"noumenon\": {
      \"command\": \"noum\",
      \"args\": [\"mcp\"]
    }
  }
}")

(def code-config
  "{
  \"mcpServers\": {
    \"noumenon\": {
      \"command\": \"noum\",
      \"args\": [\"mcp\"]
    }
  }
}")

(def tools
  [{:section "Discovery"
    :items
    [["noumenon_status"        "Entity counts. Use this to verify the repo has been imported."]
     ["noumenon_get_schema"    "Full attribute and entity-type listing."]
     ["noumenon_list_queries"  "List all named Datalog queries."]
     ["noumenon_list_databases" "All known databases with pipeline state and cost."]
     ["noumenon_search"        "TF-IDF semantic search over file/component summaries."]]}
   {:section "Querying"
    :items
    [["noumenon_query"         "Run a named Datalog query (with input args)."]
     ["noumenon_ask"           "Natural-language question answered via the Ask agent."]]}
   {:section "Pipeline"
    :items
    [["noumenon_import"        "Parse git history and files into Datomic."]
     ["noumenon_enrich"        "Resolve cross-file import edges."]
     ["noumenon_analyze"       "LLM extracts code segments per file."]
     ["noumenon_synthesize"    "Identify components and architectural layers."]
     ["noumenon_update"        "Sync to latest git state (import + enrich)."]
     ["noumenon_digest"        "Run the full pipeline end-to-end (idempotent)."]
     ["noumenon_reseed"        "Drop and rebuild a database from scratch."]]}
   {:section "Introspection"
    :items
    [["noumenon_introspect"        "Run the autonomous loop synchronously."]
     ["noumenon_introspect_start"  "Kick off introspection in the background."]
     ["noumenon_introspect_status" "Check progress on a running introspection."]
     ["noumenon_introspect_stop"   "Halt a running introspection."]
     ["noumenon_introspect_history" "Past introspection runs and their deltas."]]}
   {:section "Benchmarks"
    :items
    [["noumenon_benchmark_run"      "Run benchmarks against the configured question set."]
     ["noumenon_benchmark_results"  "Get the latest run or look up by ID."]
     ["noumenon_benchmark_compare"  "Compare two runs by per-layer score deltas."]]}
   {:section "Provider Config"
    :items
    [["noumenon_llm_providers" "List configured LLM providers and defaults."]
     ["noumenon_llm_models"    "List available models per provider."]]}
   {:section "Admin"
    :items
    [["noumenon_artifact_history" "History of analyzed artifacts (prompt/model drift)."]]}])

(defn- code-block [lang body]
  [:pre [:code {:data-lang lang} body]])

(defn- walkthrough [{:keys [id title intro config-path config-body cmd?]}]
  [:div
   [:h3 {:id id} title]
   [:p intro]
   (when cmd?
     [:p "Or run " [:code (str "noum setup " (subs id 6))]
      ". The CLI writes the file for you and adds Noumenon's CLAUDE.md guidance."])
   [:p [:strong "Config path: "] [:code config-path]]
   (code-block "json" config-body)])

(defn- tool-section [{:keys [section items]}]
  [:div
   [:h3 {:id (str "tools-" (.toLowerCase section))} section]
   [:table.md-table
    [:thead [:tr [:th "Tool"] [:th "Purpose"]]]
    (into [:tbody]
          (for [[name purpose] items]
            [:tr [:td [:code name]] [:td purpose]]))]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "setup"} "Setup"]
   (walkthrough
    {:id "setup-desktop"
     :title "Claude Desktop"
     :intro [:span "Add a Noumenon entry to Claude Desktop's MCP config and restart the app."]
     :config-path "~/Library/Application Support/Claude/claude_desktop_config.json"
     :config-body desktop-config
     :cmd? true})
   (walkthrough
    {:id "setup-code"
     :title "Claude Code"
     :intro [:span "Drop a project-local " [:code ".mcp.json"]
             " in your repo. Optional: " [:code "noum setup code"]
             " also installs a PreToolUse hook that nudges the agent to call MCP first."]
     :config-path ".mcp.json (in your project root)"
     :config-body code-config
     :cmd? true})
   (walkthrough
    {:id "setup-generic"
     :title "Any MCP Client"
     :intro [:span "Spawn " [:code "noum mcp"]
             " over stdio. The launcher proxies to the local daemon."]
     :config-path "Whatever shape your client expects. Command plus args."
     :config-body desktop-config
     :cmd? false})

   [:h2 {:id "tools"} "Tool Catalog"]
   [:p
    "Twenty-five tools across discovery, querying, pipeline control, "
    "introspection, and benchmarks. All return JSON; most accept the repo "
    "name or path as the first argument."]
   (for [section tools]
     (tool-section section))

   [:div.callout
    [:p
     "MCP tool schemas are part of the OpenAPI mirror under "
     [:a {:href "/api/"} "HTTP API"] ". Same shapes, both surfaces."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Model Context Protocol"]
    [:p.lead
     "Noumenon exposes its knowledge graph as MCP tools so AI agents can query "
     "structured facts instead of scanning raw source. Works with Claude Desktop, "
     "Claude Code, or any MCP client."]
    (prose-body)]])
