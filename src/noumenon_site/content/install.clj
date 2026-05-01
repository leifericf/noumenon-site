(ns noumenon-site.content.install
  "Install + quickstart page (mounted at /get-started/)."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def providers-snippet
  "{:default-provider :glm
 :providers
 {:glm        {:base-url \"https://api.z.ai/api/paas/v4\"
               :api-key  \"glm_...\"
               :default-model \"glm-4-plus\"}
  :claude-api {:base-url \"https://api.anthropic.com/v1\"
               :api-key  \"sk-ant-...\"
               :default-model \"sonnet\"}}}")

(defn- code-block [lang body]
  [:pre [:code {:data-lang lang} body]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "quickstart"} "Quickstart"]
   [:div.steps
    [:div.step
     [:div.step-number 1]
     [:div.step-content
      [:h3 "Install the Launcher"]
      [:code "curl -sSL https://noumenon.leifericf.com/install | bash"]
      [:p.step-hint
       "Installs the " [:code "noum"] " launcher to "
       [:code "~/.local/bin"] " and verifies its checksum."]]]
    [:div.step
     [:div.step-number 2]
     [:div.step-content
      [:h3 "Run the Demo"]
      [:code "noum demo"]
      [:p.step-hint
       "Downloads a pre-built knowledge graph for the noumenon repo "
       "itself (Noumenon imports its own source as the demo). The "
       "tarball is fetched from GitHub Releases, SHA256-verified, and "
       "extracted into " [:code "~/.noumenon/data/"] ". No LLM "
       "credentials needed for queries — the analyze stage is already done."]
      [:p.step-hint "Try:"]
      [:pre [:code {:data-lang "bash"}
             "noum ask noumenon \"What are the major components?\"\nnoum ask noumenon \"Which files have the most churn?\"\nnoum query hotspots noumenon"]]]]
    [:div.step
     [:div.step-number 3]
     [:div.step-content
      [:h3 "Ask a Question About Your Own Code"]
      [:code "noum ask /path/to/your/repo \"Where is auth handled?\""]
      [:p.step-hint
       "First call digests the repo (one-time). Subsequent calls reuse the graph."]]]]

   [:h2 {:id "package-managers"} "Package Managers"]
   [:p
    "Prefer a package manager? Noumenon ships through Homebrew, Scoop, and Docker. "
    "All channels track the same release artifacts."]
   [:h3 {:id "homebrew"} "Homebrew (macOS, Linux)"]
   (code-block "bash" "brew install leifericf/noumenon/noumenon")
   [:h3 {:id "scoop"} "Scoop (Windows)"]
   (code-block "bash" "scoop bucket add leifericf https://github.com/leifericf/scoop-bucket\nscoop install noumenon")
   [:h3 {:id "docker"} "Docker"]
   (code-block "bash" "docker run --rm -v $PWD:/repo ghcr.io/leifericf/noumenon ask /repo \"...\"")
   [:p
    "For full server-mode deployments (shared graphs, role-based tokens), "
    "see " [:a {:href "/server/"} "Run as a shared service"] "."]

   [:h2 {:id "llm-provider"} "Configure an LLM Provider"]
   [:p
    "Noumenon needs an LLM API key for the analyze, synthesize, and ask stages. "
    "Demo data works without one, but real repos do not."]
   [:p
    "Configure providers via " [:code "NOUMENON_LLM_PROVIDERS_EDN"]
    " (canonical) and select the default with " [:code "NOUMENON_DEFAULT_PROVIDER"] "."]
   (code-block "clojure" providers-snippet)
   [:p
    "Built-in providers: " [:code ":glm"] " (Z.ai) and " [:code ":claude-api"]
    " (Anthropic). Discover available models with " [:code "noum llm-models"] "."]

   [:h2 {:id "interfaces"} "Other Ways to Drive It"]
   [:p
    "The same daemon answers to several front-ends. Pick whichever fits the moment:"]
   [:ul
    [:li
     [:strong "Interactive TUI."]
     " Run " [:code "noum"] " with no arguments for a menu-driven terminal "
     "interface. Repos and ask sessions populate from live data, so there's "
     "nothing to memorize."]
    [:li
     [:strong "Desktop UI (experimental)."]
     " " [:code "noum open"] " auto-downloads an early Electron preview "
     "with a graph view, drill-down, and a floating Ask overlay. Very "
     "rough — fine for poking around, not for daily driving. See "
     [:a {:href "/concepts/desktop-ui/"} "Desktop UI"] "."]
    [:li
     [:strong "Model Context Protocol."]
     " " [:code "noum setup desktop"] " or " [:code "noum setup code"]
     " wires Noumenon into Claude Desktop or Claude Code. Agents call "
     [:code "noumenon_ask"] ", " [:code "noumenon_query"]
     ", and a couple dozen other tools without loading raw source into "
     "context. See "
     [:a {:href "/mcp/"} "MCP setup"] "."]
    [:li
     [:strong "HTTP API."]
     " Headless integrations talk plain JSON to the daemon. See the "
     [:a {:href "/api/"} "HTTP API reference"] "."]]

   [:div.callout
    [:p
     "Hit a snag? " [:a {:href "https://github.com/leifericf/noumenon/issues"} "Open an issue"]
     " or check the " [:a {:href "/changelog/"} "changelog"] " for recent breaking changes."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Install Noumenon"]
    [:p.lead
     "One command to install. Then point it at any git repository and ask questions."]
    (prose-body)]])
