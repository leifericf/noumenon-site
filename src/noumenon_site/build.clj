(ns noumenon-site.build
  "Site build orchestrator.

   Defines the Stasis page map and exports to _site/.
   Run via: clj -X:build"
  (:require [clojure.java.io :as io]
            [stasis.core :as stasis]
            [noumenon-site.render :as render]
            [noumenon-site.content.landing :as landing]))

(def openapi-source-url
  "https://raw.githubusercontent.com/leifericf/noumenon/main/resources/openapi.yaml")

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

(defn- fetch-openapi!
  "Fetch the canonical openapi.yaml from leifericf/noumenon and write
   it to out-dir/openapi.yaml. Single source of truth — no committed
   copy in this repo."
  [out-dir]
  (let [dest (io/file out-dir "openapi.yaml")]
    (with-open [in  (io/input-stream openapi-source-url)
                out (io/output-stream dest)]
      (io/copy in out))))

(defn build-site!
  "Entry point for clj -X:build. Exports pages to out-dir (default _site)."
  [& {:keys [out-dir] :or {out-dir "_site"}}]
  (println "Building noumenon-site into" out-dir "...")
  (stasis/empty-directory! out-dir)
  (stasis/export-pages (pages) out-dir)
  (copy-public-assets! out-dir)
  (try
    (fetch-openapi! out-dir)
    (println "  Fetched openapi.yaml from" openapi-source-url)
    (catch Exception e
      (println "  Warning: failed to fetch openapi.yaml:" (.getMessage e))))
  (println "Site built successfully!")
  (println (str "  " (count (pages)) " pages generated"))
  (println (str "  Open " out-dir "/index.html to preview")))
