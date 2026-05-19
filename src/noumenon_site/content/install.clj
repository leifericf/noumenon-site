(ns noumenon-site.content.install
  "Install + quickstart page (mounted at /get-started/)."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def env-snippet-anthropic
  "export NOUMENON_LLM_BASE_URL=https://api.anthropic.com
export NOUMENON_LLM_API_KEY=sk-ant-...
export NOUMENON_LLM_MODEL=claude-sonnet-4-6-20250514")

(def env-snippet-openrouter
  "export NOUMENON_LLM_BASE_URL=https://openrouter.ai/api/v1
export NOUMENON_LLM_API_KEY=sk-or-...
export NOUMENON_LLM_MODEL=anthropic/claude-sonnet-4-5")

(def env-snippet-litellm
  "export NOUMENON_LLM_BASE_URL=http://localhost:4000
export NOUMENON_LLM_API_KEY=sk-litellm-master-...
export NOUMENON_LLM_MODEL=<the-name-defined-in-litellm-config.yaml>")

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

   [:h2 {:id "llm-endpoint"} "Configure an LLM Endpoint"]
   [:p
    "Noumenon needs an LLM API key for the analyze, synthesize, and ask stages. "
    "Demo data works without one, but real repos do not."]
   [:p
    "Point Noumenon at any endpoint that speaks the Anthropic Messages API: "
    "Anthropic directly, a router like " [:a {:href "https://openrouter.ai"} "OpenRouter"]
    " or self-hosted " [:a {:href "https://github.com/BerriAI/litellm"} "LiteLLM"]
    ", or any compatible gateway. Three env vars, two of them required:"]
   [:ul
    [:li [:code "NOUMENON_LLM_BASE_URL"] " — endpoint URL (required)"]
    [:li [:code "NOUMENON_LLM_API_KEY"] " — bearer/x-api-key value (required)"]
    [:li [:code "NOUMENON_LLM_MODEL"] " — default model id, overridable per-call with "
     [:code "--model"] " (optional)"]]
   [:h3 {:id "endpoint-anthropic"} "Anthropic directly"]
   (code-block "bash" env-snippet-anthropic)
   [:h3 {:id "endpoint-openrouter"} "OpenRouter (multi-model routing)"]
   (code-block "bash" env-snippet-openrouter)
   [:h3 {:id "endpoint-litellm"} "LiteLLM (self-hosted proxy)"]
   (code-block "bash" env-snippet-litellm)
   [:p
    "For local use, " [:code "noum setup"] " will prompt for these and write "
    "them to " [:code "~/.noumenon/credentials"]
    ". Noumenon reads that file directly — no shell sourcing needed."]
   [:p
    "Noumenon does not validate or alias the model id. Whatever you pass goes "
    "verbatim to the upstream endpoint."]

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
