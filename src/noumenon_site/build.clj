(ns noumenon-site.build
  "Site build orchestrator.

   Defines the Stasis page map and exports to _site/.
   Run via: clj -X:build"
  (:require [clojure.java.io :as io]
            [stasis.core :as stasis]
            [noumenon-site.render :as render]
            [noumenon-site.paths :as paths]
            [noumenon-site.content.landing :as landing]))

(defn pages
  "Returns a Stasis page map: {path -> (fn [ctx] html-string)}."
  []
  {"/index.html"
   (fn [_]
     (render/html-page {} (landing/landing-page)))})

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
  (let [source (paths/noumenon-source noumenon-source)]
    (println "Building noumenon-site into" out-dir "...")
    (println "  Sourcing cross-repo content from" source)
    (stasis/empty-directory! out-dir)
    (stasis/export-pages (pages) out-dir)
    (copy-public-assets! out-dir)
    (copy-openapi! out-dir source)
    (println "Site built successfully!")
    (println (str "  " (count (pages)) " pages generated"))
    (println (str "  Open " out-dir "/index.html to preview"))))
