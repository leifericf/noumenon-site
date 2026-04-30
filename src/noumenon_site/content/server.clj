(ns noumenon-site.content.server
  "Server-mode deploy page — parsed from leifericf/noumenon DEPLOY.md."
  (:require [noumenon-site.parse.deploy :as parse]))

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Run Noumenon as a shared service"]
    [:p.lead
     "Deploy a centralized Noumenon instance for your team or organization. "
     "Mirrored from "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/DEPLOY.md"} "DEPLOY.md"]
     " in the source repo."]
    (if-let [blocks (parse/parsed)]
      (into [:div.prose] blocks)
      [:p [:em "Deploy guide source not available."]])]])
