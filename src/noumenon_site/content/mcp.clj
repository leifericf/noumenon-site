(ns noumenon-site.content.mcp
  "MCP setup walkthroughs + tool list."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as render]))

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

(defn- tool-slug [section]
  (str "tools-" (-> section .toLowerCase (.replace " " "-"))))

(defn- tool-section [{:keys [section items]}]
  [:div.list-section {:id (tool-slug section)}
   [:h3 (str section " · " (count items))]
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

   [:h3 {:id "search-vs-ask"} "Cheap, Medium, Expensive"]
   [:p
    "The three tools that surface knowledge from a digested repo sit on "
    "a clear cost gradient. Reach for the cheapest one that does the job."]
   [:ul
    [:li
     [:strong [:code "noumenon_search"] " — milliseconds, zero LLM calls. "]
     "TF-IDF cosine similarity over per-file and per-component "
     "summaries. Returns ranked file paths with scores. The cheapest "
     "way to find \"which files are about X.\" Use this when you want "
     "discovery without the price of a full agent loop."]
    [:li
     [:strong [:code "noumenon_query"] " — milliseconds, zero LLM calls. "]
     "Run a named or raw Datalog query. The right tool when you know "
     "the question maps to existing structure (\"hotspots,\" \"files-by-layer,\" "
     "\"top-contributors\") and want the structured result back."]
    [:li
     [:strong [:code "noumenon_ask"] " — seconds, multiple LLM calls. "]
     "Iterative agent. TF-IDF warm start, routing-model hint, then a "
     "loop over Datalog queries until it has enough to answer. Use when "
     "the question is open-ended or requires composing multiple facts. "
     "See " [:a {:href "/concepts/ask/"} "Ask"] " for how it works."]]
   (for [section tools]
     (tool-section section))

   [:div.callout
    [:p
     "MCP tool schemas are part of the OpenAPI mirror under "
     [:a {:href "/api/"} "HTTP API"] ". Same shapes, both surfaces."]]])

(defn- sidebar []
  (render/sidebar-nav
   [{:heading "On this page"
     :items [{:href "#setup"          :label "Setup"}
             {:href "#setup-desktop"  :label "Claude Desktop"}
             {:href "#setup-code"     :label "Claude Code"}
             {:href "#setup-generic"  :label "Any MCP Client"}
             {:href "#tools"          :label "Tool Catalog"}
             {:href "#search-vs-ask"  :label "Cheap / Medium / Expensive"}]}
    {:heading "Tool Categories"
     :items (for [{:keys [section items]} tools]
              {:href            (str "#" (tool-slug section))
               :data-section-id (tool-slug section)
               :label           (str section " · " (count items))})}]))

(defn page []
  [:section.docs
   [:div.container-wide
    [:h1.docs-title "Model Context Protocol"]
    [:p.lead
     "Noumenon exposes its knowledge graph as MCP tools so AI agents can query "
     "structured facts instead of scanning raw source. Works with Claude Desktop, "
     "Claude Code, or any MCP client."]
    [:div.docs-layout
     (sidebar)
     [:div.docs-content (prose-body)]]]])
