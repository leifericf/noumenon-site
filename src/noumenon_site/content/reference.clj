(ns noumenon-site.content.reference
  "Reference hub.")

(def cards
  [{:href "/queries/" :title "Queries"
    :body "Catalog of named Datalog queries, generated from the source repo."}
   {:href "/api/" :title "HTTP API"
    :body "OpenAPI spec rendered with Scalar."}
   {:href "/mcp/" :title "MCP"
    :body "Model Context Protocol setup and tool list."}])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Reference"]
    [:p.lead "API surface for humans, agents, and LLMs."]
    [:div.hub-grid
     (for [{:keys [href title body]} cards]
       [:a.hub-card {:href href}
        [:h3 title]
        [:p body]])]]])
