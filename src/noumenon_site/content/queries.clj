(ns noumenon-site.content.queries
  "Queries catalog page — auto-generated from resources/queries/*.edn."
  (:require [clojure.string :as str]
            [noumenon-site.parse.queries :as parse]))

(defn- query-row [{:keys [name description inputs]}]
  (let [inputs-str (when (seq inputs)
                     (str/join ", " (map (comp str symbol) inputs)))]
    [:tr {:data-name name :data-desc (or description "")}
     [:td [:span.query-name name]]
     [:td description]
     [:td (when inputs-str [:span.query-inputs inputs-str])]]))

(defn page []
  (let [queries (parse/all)]
    [:section.docs
     [:div.container
      [:h1.docs-title "Query catalog"]
      [:p.lead
       (count queries) " named Datalog queries shipped with Noumenon. "
       "Run with " [:code "noum query <name> [args...]"]
       " or invoke via MCP " [:code "noumenon_query"] "."]
      [:input.queries-filter
       {:id          "queries-filter"
        :type        "search"
        :placeholder "Filter queries..."
        :aria-label  "Filter queries"}]
      [:table.queries-table
       [:thead
        [:tr [:th "Name"] [:th "Description"] [:th "Inputs"]]]
       (into [:tbody] (map query-row queries))]
      [:div.callout {:style "margin-top: 2rem;"}
       [:p
        "Source-of-truth lives at "
        [:a {:href "https://github.com/leifericf/noumenon/tree/main/resources/queries"}
         "resources/queries/"]
        " in the Noumenon repo. Submit a PR to add a query, then it shows up here on the next site rebuild."]]]]))
