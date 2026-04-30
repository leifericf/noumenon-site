(ns user
  "REPL development setup.
   Start a local dev server with (start!), stop with (stop!)."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [ring.adapter.jetty :as jetty]
            [noumenon-site.build :as build]))

(defonce server (atom nil))

(defn- resolve-uri [uri]
  (cond
    (= uri "/")              "/index.html"
    (str/ends-with? uri "/") (str uri "index.html")
    (str/includes? uri ".")  uri
    :else                    (str uri "/index.html")))

(defn- content-type [uri]
  (cond
    (str/ends-with? uri ".svg")  "image/svg+xml"
    (str/ends-with? uri ".yaml") "text/yaml"
    (str/ends-with? uri ".css")  "text/css"
    (str/ends-with? uri ".js")   "application/javascript"
    :else                        "text/plain"))

(defn- file-asset
  "Serve <root>/<uri> if the file exists."
  [root uri]
  (let [f (io/file root (subs uri 1))]
    (when (and (.isFile f) (not (str/blank? (subs uri 1))))
      {:status  200
       :headers {"Content-Type" (content-type uri)}
       :body    f})))

(defn app [request]
  (let [pages    (build/pages)
        uri      (:uri request)
        page-key (resolve-uri uri)]
    (or (when-let [page-fn (get pages page-key)]
          {:status  200
           :headers {"Content-Type" "text/html; charset=utf-8"}
           :body    (page-fn {})})
        (file-asset "resources/public" uri)
        ;; Fallback: build artifacts only present after `clj -X:build`
        ;; (e.g. /openapi.yaml is fetched at build time into _site/).
        (file-asset "_site" uri)
        {:status  404
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body    "<h1>404</h1>"})))

(defn start!
  ([] (start! 3000))
  ([port]
   (when @server (.stop @server))
   (reset! server (jetty/run-jetty #'app {:port port :join? false}))
   (println (str "Dev server: http://localhost:" port))))

(defn stop! []
  (when @server
    (.stop @server)
    (reset! server nil)
    (println "Stopped.")))
