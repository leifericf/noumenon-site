(ns noumenon-site.content.api
  "OpenAPI reference page (Scalar embed). Phase 2 placeholder.")

(defn page []
  [:section
   [:div.container
    [:h1 "HTTP API"]
    [:p "Scalar-rendered OpenAPI loads here."]
    [:div.scalar-frame {:id "scalar-target"}]]])
