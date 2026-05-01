(ns noumenon-site.content.concepts.desktop-ui
  "Desktop UI concept page. Framed as a very early proof of concept.")

(defn- prose-body []
  [:div.prose
   [:div.callout {:style "border-left-color: #f0c674;"}
    [:p
     [:strong "Very early proof of concept. "]
     "The desktop UI is a preview of where Noumenon is heading, not a "
     "supported surface yet. Expect rough edges, breaking changes, and "
     "missing features. The CLI and MCP integrations are the supported "
     "ways to use Noumenon today; pick this up when you want to look "
     "rather than ship."]]

   [:h2 {:id "how-to-try"} "How to Try It"]
   [:p
    "Run " [:code "noum open"] ". On first call the launcher fetches "
    "the latest packaged build from "
    [:a {:href "https://github.com/leifericf/noumenon-app/releases"}
     [:code "leifericf/noumenon-app"] " releases"]
    " (a " [:code "Noumenon-*.dmg"] " on macOS, "
    [:code "Noumenon-*.AppImage"] " on Linux), extracts it under "
    [:code "~/.noumenon/ui/"] ", and launches. Subsequent calls reuse "
    "the cached app."]
   [:p
    "The desktop app talks to the same local daemon as the CLI and the "
    "MCP server. There's nothing extra to start; if " [:code "noum"]
    " works, " [:code "noum open"] " works."]

   [:h2 {:id "preview"} "What's in the Preview Today"]
   [:ul
    [:li
     [:strong "Force-directed graph"]
     " at the macro level. Nodes are components, edges are dependencies "
     "from the synthesize stage. Drag nodes around, hover for component "
     "summaries."]
    [:li
     [:strong "Drill-down"]
     " from a component into the files it owns, then from a file into "
     "its segments (functions, classes, types) with complexity ratings "
     "and code smells inline."]
    [:li
     [:strong "Floating Ask overlay"]
     " that talks to the same Ask agent as " [:code "noum ask"] ". "
     "Useful when you want to query while staring at the graph."]]
   [:p
    "Things that are " [:em "not"] " in the preview yet: editing "
    "settings, managing tokens for shared instances, viewing benchmark "
    "or introspect runs, and a bunch of polish you'd want before "
    "calling it a real GUI. The CLI covers all of those today."]

   [:h2 {:id "source"} "Where the Source Lives"]
   [:p
    "The Electron app is its own repo: "
    [:a {:href "https://github.com/leifericf/noumenon-app"}
     "leifericf/noumenon-app"]
    ". Issues and feature requests are welcome there. The auto-download "
    "logic on the launcher side is in "
    [:a {:href "https://github.com/leifericf/noumenon/blob/main/launcher/src/noum/electron.clj"}
     "launcher/src/noum/electron.clj"]
    "."]

   [:div.callout
    [:p
     "If you'd rather not download an Electron app on first run, skip "
     [:code "noum open"] " and stay with the CLI, MCP, or HTTP API. "
     "Nothing in the desktop UI is exclusive; it's a different lens on "
     "the same graph."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Desktop UI"]
    [:p.lead
     "Noumenon ships an experimental Electron preview that visualizes "
     "the knowledge graph. " [:code "noum open"] " auto-downloads it "
     "and points it at the local daemon."]
    (prose-body)]])
