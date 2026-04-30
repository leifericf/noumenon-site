(ns noumenon-site.content.landing
  "Landing page body. Phase 1 placeholder — Phase 2 will redesign.")

(defn landing-page []
  [:div
   [:section.hero
    [:h1 "Precise, grounded answers about your codebase."]
    [:p.hero-tagline
     "Noumenon compiles git repositories into a Datomic knowledge graph. "
     "AI agents query structured facts — commits, files, code segments, components — "
     "instead of scanning raw source into context windows. Faster, cheaper, more accurate."]]

   [:section.section
    [:h2.section-title "Install"]
    [:pre [:code "curl -sSL https://noumenon.leifericf.com/install | bash"]]
    [:p
     "Or via " [:a {:href "https://brew.sh"} "Homebrew"] ": "
     [:code "brew install leifericf/noumenon/noumenon"] ". "
     "Or download a binary from "
     [:a {:href "https://github.com/leifericf/noumenon/releases"} "GitHub releases"] "."]]

   [:section.section
    [:h2.section-title "Try the demo"]
    [:pre [:code "noum demo\nnoum ask noumenon \"Describe the architecture\""]]
    [:p "No credentials needed — pre-built knowledge graph."]]

   [:section.section
    [:h2.section-title "Build your own"]
    [:pre [:code "noum digest /path/to/repo --provider glm\nnoum ask /path/to/repo \"Which files are the biggest risk hotspots?\""]]
    [:p
     "Pipeline stages run individually too: "
     [:code "noum import"] ", "
     [:code "noum enrich"] ", "
     [:code "noum analyze"] ", "
     [:code "noum synthesize"] ", "
     [:code "noum embed"] "."]]

   [:section.section
    [:h2.section-title "MCP server"]
    [:pre [:code "noum setup desktop    # Claude Desktop\nnoum setup code       # Claude Code"]]
    [:p
     "Or run " [:code "noum serve"] " manually. The CLI and MCP server expose the same capabilities; "
     "see the " [:a {:href "/openapi.yaml"} "OpenAPI spec"] " for the HTTP API."]]

   [:section.section
    [:h2.section-title "Visual UI"]
    [:pre [:code "noum open"]]
    [:p
     "Electron desktop app with force-directed graph visualization, three-level drill-down, "
     "and a floating Ask overlay. Source: "
     [:a {:href "https://github.com/leifericf/noumenon-app"} "leifericf/noumenon-app"] "."]]

   [:section.section
    [:h2.section-title "Server mode"]
    [:p
     "Run Noumenon as a shared service for your team or organization. "
     "Up to 200 concurrent users, role-based access, auto-refresh from git. "
     "See " [:a {:href "https://github.com/leifericf/noumenon/blob/main/DEPLOY.md"} "DEPLOY.md"] " for setup."]]])
