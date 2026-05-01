(ns noumenon-site.content.api
  "HTTP API reference page — a simple grouped endpoint list parsed
   from the OpenAPI YAML. No interactive console, no code samples,
   no third-party renderer."
  (:require [clojure.string :as str]
            [noumenon-site.parse.openapi :as parse]))

(def groups
  ;; [Heading prefix-prefix-... ] in display order.
  [["Health"          ["/health"]]
   ["Pipeline"        ["/api/import" "/api/enrich" "/api/analyze"
                       "/api/synthesize" "/api/update" "/api/digest"
                       "/api/reseed"]]
   ["Querying"        ["/api/ask" "/api/query" "/api/queries"
                       "/api/schema" "/api/status" "/api/search"]]
   ["Databases"       ["/api/databases"]]
   ["Benchmarks"      ["/api/benchmark"]]
   ["Introspection"   ["/api/introspect"]]
   ["Admin"           ["/api/artifacts"]]])

(defn- group-for
  [path]
  (some (fn [[heading prefixes]]
          (when (some #(str/starts-with? path %) prefixes)
            heading))
        groups))

(defn- by-group
  [endpoints]
  (->> endpoints
       (group-by (comp group-for :path))
       (filter (comp some? key))
       (into {})))

(defn- group-table [endpoints]
  [:table.md-table
   [:thead [:tr [:th "Method"] [:th "Path"] [:th "Summary"]]]
   (into [:tbody]
         (for [{:keys [method path summary]} endpoints]
           [:tr
            [:td [:span {:class (str "http-method http-" method)}
                  (str/upper-case method)]]
            [:td [:code path]]
            [:td summary]]))])

(defn- prose-body [endpoints]
  (let [grouped (by-group endpoints)
        order   (map first groups)]
    [:div.prose
     [:h2 {:id "endpoints"} (str (count endpoints) " Endpoints")]
     [:p
      "Every endpoint returns JSON envelope " [:code "{ok, data}"] " or "
      [:code "{ok:false, error}"] ". Long-running POSTs stream progress as "
      "Server-Sent Events when called with " [:code "Accept: text/event-stream"] "."]
     (for [heading order
           :let [items (->> (get grouped heading [])
                            (sort-by (juxt :path :method)))]
           :when (seq items)]
       [:div
        [:h2 {:id (-> heading str/lower-case (str/replace " " "-"))} heading]
        (group-table items)])
     [:div.callout
      [:p
       "Full request/response shapes live in the OpenAPI spec: "
       [:a {:href "/openapi.yaml"} "/openapi.yaml"]
       ". Source-of-truth in the noumenon repo: "
       [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/openapi.yaml"}
        "resources/openapi.yaml"]
       "."]]]))

(defn page []
  (let [endpoints (parse/endpoints)]
    [:section.docs
     [:div.container
      [:h1.docs-title "HTTP API"]
      [:p.lead
       "Noumenon's daemon speaks plain JSON over HTTP. The CLI launcher and "
       "the desktop UI both go through this surface; you can too."]
      (if (seq endpoints)
        (prose-body endpoints)
        [:p [:em "OpenAPI spec source not available."]])]]))
