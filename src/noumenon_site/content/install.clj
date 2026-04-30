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

(defn page []
  [:section
   [:div.container
    [:h1 "Install Noumenon"]
    [:p.lead
     "One command to install. Then point it at any git repository and ask questions."]

    [:h2 {:id "quickstart"} "Quickstart"]
    [:div.steps
     [:div.step
      [:div.step-number 1]
      [:div.step-content
       [:h3 "Install the launcher"]
       [:code "curl -sSL https://noumenon.leifericf.com/install | bash"]
       [:p.step-hint
        "Installs the " [:code "noum"] " launcher to "
        [:code "~/.local/bin"] " and verifies its checksum."]]]
     [:div.step
      [:div.step-number 2]
      [:div.step-content
       [:h3 "Run the demo"]
       [:code "noum demo"]
       [:p.step-hint
        "Pre-built knowledge graph with sample questions. No LLM credentials needed."]]]
     [:div.step
      [:div.step-number 3]
      [:div.step-content
       [:h3 "Ask a question about your own code"]
       [:code "noum ask /path/to/your/repo \"Where is auth handled?\""]
       [:p.step-hint
        "First call digests the repo (one-time). Subsequent calls reuse the graph."]]]]

    [:h2 {:id "package-managers"} "Package managers"]
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
     "For full server-mode deployments — shared graphs, role-based tokens — "
     "see " [:a {:href "/server/"} "Run as a shared service"] "."]

    [:h2 {:id "llm-provider"} "Configure an LLM provider"]
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

    [:h2 {:id "mcp"} "Use Noumenon from your AI agent"]
    [:p
     "Noumenon ships with a Model Context Protocol server. Your agent calls "
     [:code "noumenon_ask"] ", " [:code "noumenon_query"]
     ", and 30+ other tools without ever loading raw source into context."]
    [:p
     [:a.btn.btn-secondary {:href "/mcp/"} "MCP setup walkthrough →"]]

    [:div.callout
     [:p
      "Hit a snag? " [:a {:href "https://github.com/leifericf/noumenon/issues"} "Open an issue"]
      " or check the " [:a {:href "/changelog/"} "changelog"] " for recent breaking changes."]]]])
