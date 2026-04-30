(ns noumenon-site.content.api
  "HTTP API reference page — Scalar-rendered OpenAPI."
  (:require [hiccup2.core :as h]))

(def scalar-config
  (h/raw "{\"theme\":\"deepSpace\",\"layout\":\"modern\",\"hideDownloadButton\":false}"))

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "HTTP API"]
    [:p.lead
     "Noumenon ships a REST API for headless integrations and the desktop UI. "
     "Spec source: "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/openapi.yaml"}
      "resources/openapi.yaml"]
     ". Mirrored to "
     [:a {:href "/openapi.yaml"} "/openapi.yaml"]
     " on every site rebuild."]
    [:div.scalar-frame
     [:script {:id              "api-reference"
               :data-url        "/openapi.yaml"
               :data-configuration scalar-config}]
     [:script {:src "https://cdn.jsdelivr.net/npm/@scalar/api-reference"}]]]])
