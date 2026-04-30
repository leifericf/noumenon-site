(ns noumenon-site.content.changelog
  "Changelog page (parsed from CHANGES.md). Phase 2 placeholder.")

(defn page []
  [:section
   [:div.container
    [:h1 "Changelog"]
    [:p "Parsed from CHANGES.md in the source repo."]]])
