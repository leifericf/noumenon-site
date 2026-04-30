(ns noumenon-site.paths
  "Resolve the local checkout of leifericf/noumenon used as the
   single source of truth for cross-repo content (queries, CHANGES,
   DEPLOY, openapi.yaml)."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private env-var "NOUMENON_SOURCE")

(def ^:private fallback
  (str (System/getProperty "user.home") "/Code/noumenon"))

(defn- valid-source?
  [path]
  (and path
       (.isDirectory (io/file path))
       (.isFile (io/file path "resources/openapi.yaml"))))

(defn noumenon-source
  "Return the absolute path to the noumenon source checkout, or throw
   if neither NOUMENON_SOURCE nor the ~/Code/noumenon fallback resolves
   to a directory containing resources/openapi.yaml."
  ([]
   (noumenon-source nil))
  ([explicit]
   (let [candidates (->> [explicit (System/getenv env-var) fallback]
                         (remove nil?)
                         distinct)]
     (or (first (filter valid-source? candidates))
         (throw (ex-info
                 (str "Cannot locate noumenon source. Tried: "
                      (str/join ", " candidates)
                      ". Set " env-var " or clone leifericf/noumenon "
                      "next to this repo.")
                 {:candidates (vec candidates)}))))))
