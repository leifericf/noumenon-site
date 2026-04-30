(ns noumenon-site.content.changelog
  "Changelog page — parsed from leifericf/noumenon CHANGES.md."
  (:require [noumenon-site.parse.changelog :as parse]))

(defn page []
  [:section
   [:div.container
    [:h1 "Changelog"]
    [:p.lead
     "All notable changes to Noumenon, mirrored from "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/CHANGES.md"} "CHANGES.md"]
     " in the source repo."]
    (if-let [blocks (parse/parsed)]
      (into [:div.markdown] blocks)
      [:p [:em "Changelog source not available."]])]])
