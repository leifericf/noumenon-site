(ns noumenon-site.parse.changelog
  "Read leifericf/noumenon CHANGES.md from the resolved noumenon source
   and return parsed Hiccup blocks."
  (:require [clojure.java.io :as io]
            [noumenon-site.parse.markdown :as md]
            [noumenon-site.paths :as paths]))

(defn parsed
  "Return parsed Hiccup blocks for CHANGES.md. Source defaults to the
   resolved noumenon checkout."
  ([] (parsed (paths/noumenon-source)))
  ([source]
   (let [f (io/file source "CHANGES.md")]
     (when (.isFile f)
       (md/parse (slurp f))))))
