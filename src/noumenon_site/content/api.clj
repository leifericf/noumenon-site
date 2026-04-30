(ns noumenon-site.content.api
  "HTTP API reference page — Scalar-rendered OpenAPI."
  (:require [hiccup2.core :as h]))

(def scalar-script
  "import { createApiReference } from 'https://cdn.jsdelivr.net/npm/@scalar/api-reference@latest/dist/browser/standalone.js';
createApiReference('#scalar-target', {
  url: '/openapi.yaml',
  theme: 'deepSpace',
  hideDownloadButton: false,
  layout: 'modern'
});")

(defn page []
  [:section
   [:div.container
    [:h1 "HTTP API"]
    [:p.lead
     "Noumenon ships a REST API for headless integrations and the desktop UI. "
     "Spec source: "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/resources/openapi.yaml"}
      "resources/openapi.yaml"]
     ". Mirrored to "
     [:a {:href "/openapi.yaml"} "/openapi.yaml"]
     " on every site rebuild."]
    [:div.scalar-frame {:id "scalar-target"}]
    [:script {:type "module"} (h/raw scalar-script)]]])
