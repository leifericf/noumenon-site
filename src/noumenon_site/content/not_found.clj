(ns noumenon-site.content.not-found
  "404 page body.")

(defn page []
  [:section
   [:div.container.section-center
    [:h1 "Not found"]
    [:p "That page is gone or never existed. " [:a {:href "/"} "Return home"] "."]]])
