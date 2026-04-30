(ns noumenon-site.content.concepts
  "Concepts hub.")

(def cards
  [{:href "/concepts/knowledge-graph/" :title "Knowledge graph"
    :body "Three levels of structure compiled from your repo."}
   {:href "/concepts/pipeline/" :title "Pipeline"
    :body "Five stages from import through synthesis."}
   {:href "/concepts/introspect/" :title "Introspect"
    :body "Autonomous loop that improves the graph over time."}
   {:href "/concepts/benchmarks/" :title "Benchmarks"
    :body "How we measure grounded answer quality."}])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Concepts"]
    [:p.lead "How Noumenon works, end to end."]
    [:div.hub-grid
     (for [{:keys [href title body]} cards]
       [:a.hub-card {:href href}
        [:h3 title]
        [:p body]])]]])
