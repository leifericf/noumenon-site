(ns noumenon-site.parse.queries
  "Read every .edn under <noumenon-source>/resources/queries/ and return
   a sorted vector of query metadata maps."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [noumenon-site.paths :as paths]))

(defn- read-query
  [f]
  (try
    (let [{:keys [name description inputs]} (edn/read-string (slurp f))]
      {:name        (or name (-> f .getName (str/replace #"\.edn$" "")))
       :description (or description "")
       :inputs      (or inputs [])
       :file        (.getName f)})
    (catch Exception _ nil)))

(defn all
  "Return a vector of {:name :description :inputs :file} sorted by name."
  ([] (all (paths/noumenon-source)))
  ([source]
   (let [dir (io/file source "resources/queries")]
     (when (.isDirectory dir)
       (->> (file-seq dir)
            (filter #(and (.isFile %) (.endsWith (.getName %) ".edn")))
            (keep read-query)
            (sort-by :name)
            vec)))))
