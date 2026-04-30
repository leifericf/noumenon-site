(ns noumenon-site.parse.openapi
  "Tiny line-based parser for the noumenon OpenAPI 3.1 YAML — pulls out
   {:method :path :summary} for every endpoint. Sufficient for our
   hand-written spec; we are not trying to be a real YAML parser."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [noumenon-site.paths :as paths]))

(def ^:private path-re    #"^  (/.+):\s*$")
(def ^:private method-re  #"^    (get|post|put|delete|patch):\s*$")
(def ^:private summary-re #"^      summary:\s*(.+?)\s*$")

(defn- match
  [re s]
  (some-> (re-matches re s) second))

(defn- parse-endpoints
  [yaml]
  (loop [lines    (str/split-lines yaml)
         in?      false
         path     nil
         method   nil
         endpoints []]
    (if-let [line (first lines)]
      (let [rest-lines (rest lines)]
        (cond
          (= "paths:" line)
          (recur rest-lines true path method endpoints)

          ;; Any top-level key after entering paths means we're done.
          (and in? (re-matches #"^[A-Za-z].*$" line))
          endpoints

          :else
          (if-let [p (and in? (match path-re line))]
            (recur rest-lines in? p nil endpoints)
            (if-let [m (and in? (match method-re line))]
              (recur rest-lines in? path m endpoints)
              (if-let [s (and in? path method (match summary-re line))]
                (recur rest-lines in? path nil
                       (conj endpoints
                             {:method method :path path :summary s}))
                (recur rest-lines in? path method endpoints))))))
      endpoints)))

(defn endpoints
  "Return all parsed endpoints from the local openapi.yaml."
  ([] (endpoints (paths/noumenon-source)))
  ([source]
   (let [f (io/file source "resources/openapi.yaml")]
     (when (.isFile f)
       (parse-endpoints (slurp f))))))
