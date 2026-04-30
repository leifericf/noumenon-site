(ns noumenon-site.content.changelog
  "Changelog page — parsed from leifericf/noumenon CHANGES.md."
  (:require [noumenon-site.parse.changelog :as parse]))

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Changelog"]
    [:p.lead
     "All notable changes to Noumenon, mirrored from "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/CHANGES.md"} "CHANGES.md"]
     " in the source repo."]
    (if-let [blocks (parse/parsed)]
      (into [:div.prose] blocks)
      [:p [:em "Changelog source not available."]])]])
