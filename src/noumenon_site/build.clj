(ns noumenon-site.build
  "Site build orchestrator.

   Defines the Stasis page map and exports to _site/.
   Run via: clj -X:build"
  (:require [clojure.java.io :as io]
            [stasis.core :as stasis]
            [noumenon-site.render :as render]
            [noumenon-site.paths :as paths]
            [noumenon-site.content.landing :as landing]
            [noumenon-site.content.install :as install]
            [noumenon-site.content.concepts :as concepts]
            [noumenon-site.content.concepts.knowledge-graph :as c-kg]
            [noumenon-site.content.concepts.pipeline :as c-pipeline]
            [noumenon-site.content.concepts.introspect :as c-introspect]
            [noumenon-site.content.concepts.benchmarks :as c-benchmarks]
            [noumenon-site.content.concepts.ask :as c-ask]
            [noumenon-site.content.concepts.source-control :as c-source-control]
            [noumenon-site.content.concepts.data-safety :as c-data-safety]
            [noumenon-site.content.concepts.desktop-ui :as c-desktop-ui]
            [noumenon-site.content.reference :as reference]
            [noumenon-site.content.queries :as queries]
            [noumenon-site.content.cli :as cli]
            [noumenon-site.content.schema :as schema]
            [noumenon-site.content.api :as api]
            [noumenon-site.content.mcp :as mcp]
            [noumenon-site.content.server :as server]
            [noumenon-site.content.changelog :as changelog]
            [noumenon-site.content.not-found :as not-found]))

(defn- render-page
  [{:keys [title description active-page show-banner?] :as opts} body-fn]
  (fn [_]
    (render/html-page
     (cond-> {}
       title             (assoc :title title)
       description       (assoc :description description)
       active-page       (assoc :active-page active-page)
       (some? show-banner?) (assoc :show-banner? show-banner?))
     (body-fn))))

(defn pages
  "Returns a Stasis page map: {path -> (fn [ctx] html-string)}."
  []
  {"/index.html"
   (render-page {:active-page :home}
                landing/landing-page)

   "/get-started/index.html"
   (render-page {:title "Install" :active-page :install}
                install/page)

   "/concepts/index.html"
   (render-page {:title "Concepts" :active-page :concepts}
                concepts/page)

   "/concepts/knowledge-graph/index.html"
   (render-page {:title "Knowledge graph" :active-page :concepts}
                c-kg/page)

   "/concepts/pipeline/index.html"
   (render-page {:title "Pipeline" :active-page :concepts}
                c-pipeline/page)

   "/concepts/introspect/index.html"
   (render-page {:title "Introspect" :active-page :concepts}
                c-introspect/page)

   "/concepts/benchmarks/index.html"
   (render-page {:title "Benchmarks" :active-page :concepts}
                c-benchmarks/page)

   "/concepts/ask/index.html"
   (render-page {:title "Ask" :active-page :concepts}
                c-ask/page)

   "/concepts/source-control/index.html"
   (render-page {:title "Source control" :active-page :concepts}
                c-source-control/page)

   "/concepts/data-safety/index.html"
   (render-page {:title "Data safety" :active-page :concepts}
                c-data-safety/page)

   "/concepts/desktop-ui/index.html"
   (render-page {:title "Desktop UI" :active-page :concepts}
                c-desktop-ui/page)

   "/reference/index.html"
   (render-page {:title "Reference" :active-page :reference}
                reference/page)

   "/queries/index.html"
   (render-page {:title "Queries" :active-page :reference}
                queries/page)

   "/cli/index.html"
   (render-page {:title "CLI" :active-page :reference}
                cli/page)

   "/schema/index.html"
   (render-page {:title "Schema" :active-page :reference}
                schema/page)

   "/api/index.html"
   (render-page {:title "HTTP API" :active-page :reference}
                api/page)

   "/mcp/index.html"
   (render-page {:title "MCP" :active-page :reference}
                mcp/page)

   "/server/index.html"
   (render-page {:title "Server" :active-page :server}
                server/page)

   "/changelog/index.html"
   (render-page {:title "Changelog"}
                changelog/page)

   "/404.html"
   (render-page {:title "Not found" :show-banner? false}
                not-found/page)})

(defn- copy-public-assets!
  "Copy resources/public/* into out-dir verbatim."
  [out-dir]
  (let [public-dir (io/file "resources/public")]
    (when (.isDirectory public-dir)
      (doseq [f (file-seq public-dir)
              :when (.isFile f)]
        (let [rel  (.relativize (.toPath public-dir) (.toPath f))
              dest (io/file out-dir (str rel))]
          (.mkdirs (.getParentFile dest))
          (io/copy f dest))))))

(defn- copy-openapi!
  "Copy openapi.yaml from the local noumenon source checkout into
   out-dir. The noumenon repo remains the single source of truth."
  [out-dir source]
  (let [src  (io/file source "resources/openapi.yaml")
        dest (io/file out-dir "openapi.yaml")]
    (.mkdirs (.getParentFile dest))
    (io/copy src dest)))

(defn build-site!
  "Entry point for clj -X:build. Exports pages to out-dir (default _site).
   Reads cross-repo content from noumenon-source (resolved via
   NOUMENON_SOURCE env or ~/Code/noumenon fallback when not provided)."
  [& {:keys [out-dir noumenon-source]
      :or {out-dir "_site"}}]
  (let [source (paths/noumenon-source noumenon-source)
        page-map (pages)]
    (println "Building noumenon-site into" out-dir "...")
    (println "  Sourcing cross-repo content from" source)
    (stasis/empty-directory! out-dir)
    (stasis/export-pages page-map out-dir)
    (copy-public-assets! out-dir)
    (copy-openapi! out-dir source)
    (println "Site built successfully!")
    (println (str "  " (count page-map) " pages generated"))
    (println (str "  Open " out-dir "/index.html to preview"))))
