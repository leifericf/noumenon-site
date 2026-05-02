(ns noumenon-site.content.api
  "HTTP API reference page — a simple grouped endpoint list parsed
   from the OpenAPI YAML. No interactive console, no code samples,
   no third-party renderer."
  (:require [clojure.string :as str]
            [noumenon-site.parse.openapi :as parse]
            [noumenon-site.render :as render]))

(def groups
  ;; [Heading prefix-prefix-... ] in display order.
  [["Health"          ["/health"]]
   ["Pipeline"        ["/api/import" "/api/enrich" "/api/analyze"
                       "/api/synthesize" "/api/update" "/api/digest"
                       "/api/reseed"]]
   ["Branches"        ["/api/delta"]]
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

(defn- slug [heading]
  (-> heading str/lower-case (str/replace " " "-")))

(defn- non-empty-headings [endpoints]
  (let [grouped (by-group endpoints)]
    (for [heading (map first groups)
          :let [items (get grouped heading [])]
          :when (seq items)]
      [heading (count items)])))

(defn- prose-body [endpoints]
  (let [grouped (by-group endpoints)
        order   (map first groups)]
    [:div.prose
     [:h2 {:id "envelope"} "Response Shape"]
     [:p
      "Every endpoint returns a JSON envelope: " [:code "{ok: true, data: ...}"]
      " on success, " [:code "{ok: false, error: \"...\"}"] " on failure. "
      "Long-running POSTs stream progress as Server-Sent Events when called "
      "with " [:code "Accept: text/event-stream"] "."]
     (for [heading order
           :let [items (->> (get grouped heading [])
                            (sort-by (juxt :path :method)))]
           :when (seq items)]
       [:div.list-section {:id (slug heading)}
        [:h2 (str heading " · " (count items))]
        (group-table items)])
     [:div.callout
      [:p
       "Full request/response shapes live in the OpenAPI spec: "
       [:a {:href "/openapi.yaml"} "/openapi.yaml"]
       ". Source-of-truth in the noumenon repo: "
       [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/openapi.yaml"}
        "resources/openapi.yaml"]
       "."]]]))

(defn- sidebar [endpoints]
  (render/sidebar-nav
   [{:heading (str (count endpoints) " endpoints")
     :items (cons {:href "#envelope" :label "Response Shape"}
                  (for [[heading n] (non-empty-headings endpoints)]
                    {:href            (str "#" (slug heading))
                     :data-section-id (slug heading)
                     :label           (str heading " · " n)}))}]))

(defn page []
  (let [endpoints (parse/endpoints)]
    [:section.docs
     [:div.container-wide
      [:h1.docs-title "HTTP API"]
      [:p.lead
       "Noumenon's daemon speaks plain JSON over HTTP. The CLI launcher and "
       "the desktop UI both go through this surface; you can too."]
      (if (seq endpoints)
        [:div.docs-layout
         (sidebar endpoints)
         [:div.docs-content (prose-body endpoints)]]
        [:p [:em "OpenAPI spec source not available."]])]]))
