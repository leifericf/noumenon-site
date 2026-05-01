(ns noumenon-site.content.not-found
  "404 page body.")

(defn page []
  [:section.docs
   [:div.container.section-center
    [:h1.docs-title "Not Found"]
    [:p "That page is gone or never existed. " [:a {:href "/"} "Return home"] "."]]])
