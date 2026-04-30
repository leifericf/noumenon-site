(ns noumenon-site.parse.deploy
  "Read leifericf/noumenon DEPLOY.md from the resolved noumenon source
   and return parsed Hiccup blocks."
  (:require [clojure.java.io :as io]
            [noumenon-site.parse.markdown :as md]
            [noumenon-site.paths :as paths]))

(defn parsed
  "Return parsed Hiccup blocks for DEPLOY.md. Source defaults to the
   resolved noumenon checkout."
  ([] (parsed (paths/noumenon-source)))
  ([source]
   (let [f (io/file source "DEPLOY.md")]
     (when (.isFile f)
       (md/parse (slurp f))))))
