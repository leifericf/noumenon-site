(ns noumenon-site.parse.schema
  "Read the 10 schema EDN files from leifericf/noumenon's
   resources/schema/ directory and return a flat seq of attribute maps."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [noumenon-site.paths :as paths]))

(def ^:private schema-files
  ;; Order mirrors noumenon.schema/schema-files so docs read in the
  ;; same order someone reading the upstream code would.
  [["core.edn"         "Git, repo, file, directory, author"]
   ["architecture.edn" "Code segments, components, layers, semantic file analysis"]
   ["synthesis.edn"    "File-level synthesis hints populated by analyze"]
   ["artifacts.edn"    "Named queries, prompts, and example artifacts"]
   ["ask.edn"          "Ask sessions, turns, and feedback"]
   ["benchmark.edn"    "Benchmark runs, scores, and per-layer breakdowns"]
   ["introspect.edn"   "Self-improvement runs and proposed modifications"]
   ["provenance.edn"   "Confidence and provenance metadata"]
   ["auth.edn"         "Tokens (SHA-256-hashed)"]
   ["settings.edn"     "Key/value settings"]])

(defn- attr-namespace
  [ident]
  (when (keyword? ident)
    (or (namespace ident) "")))

(defn- value-type
  [m]
  (when-let [t (:db/valueType m)]
    (-> t name (str/replace #"^" ":"))))

(defn- cardinality
  [m]
  (case (:db/cardinality m)
    :db.cardinality/one  "one"
    :db.cardinality/many "many"
    nil))

(defn- unique?
  [m]
  (boolean (:db/unique m)))

(defn- attr-map
  [file-name section m]
  (when (:db/ident m)
    {:file       file-name
     :section    section
     :ident      (:db/ident m)
     :namespace  (attr-namespace (:db/ident m))
     :type       (value-type m)
     :card       (cardinality m)
     :unique     (unique? m)
     :doc        (or (:db/doc m) "")}))

(defn- read-file
  [source [file-name section]]
  (let [f (io/file source "resources/schema" file-name)]
    (when (.isFile f)
      (->> (edn/read-string (slurp f))
           (filter map?)
           (keep (partial attr-map file-name section))))))

(defn all
  "Return every schema attribute as a map, preserving file order."
  ([] (all (paths/noumenon-source)))
  ([source]
   (->> schema-files
        (mapcat (partial read-file source))
        vec)))

(defn by-namespace
  "Group attributes by ident namespace, preserving file order within each
   group. Returns a vector of [namespace [attrs ...]] pairs."
  [attrs]
  (let [namespaces (->> attrs (map :namespace) distinct)
        grouped    (group-by :namespace attrs)]
    (mapv (fn [ns] [ns (vec (get grouped ns []))]) namespaces)))
