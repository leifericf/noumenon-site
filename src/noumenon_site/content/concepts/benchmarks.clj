(ns noumenon-site.content.concepts.benchmarks
  "Benchmarks concept page — methodology + full eight-repo table."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def benchmark-rows
  [["flask"    "Python"     "13" "68%"  "41K" "8K"   "14.5s" "7.1s"]
   ["ripgrep"  "Rust"       "17" "67%"  "44K" "6K"   "14.0s" "5.3s"]
   ["ring"     "Clojure"    "28" "60%"  "27K" "4K"   "12.2s" "5.3s"]
   ["fresh"    "TypeScript" "25" "55%"  "23K" "10K"  "12.7s" "5.2s"]
   ["noumenon" "Clojure"    "16" "48%"  "30K" "6K"   "10.9s" "5.2s"]
   ["fzf"      "Go"         "22" "47%"  "43K" "1K"   "15.4s" "5.1s"]
   ["express"  "JavaScript" "25" "47%"  "39K" "7K"   "12.7s" "5.2s"]
   ["redis"    "C"          "10" "31%"  "53K" "18K"  "16.6s" "10.5s"]])

(defn- benchmark-row [[repo lang acc-w acc-with tok-w tok-with spd-w spd-with]]
  [:tr
   [:td [:code repo]] [:td lang]
   [:td.num (str acc-w " ") (h/raw "&rarr;") " " [:strong acc-with]]
   [:td.num (str tok-w " ") (h/raw "&rarr;") " " [:strong tok-with]]
   [:td.num (str spd-w " ") (h/raw "&rarr;") " " [:strong spd-with]]])

(defn- stat-cards []
  [:div.layers-grid {:style "margin: 1rem 0 2.5rem;"}
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number (h/raw "2.7&times;")]
           [:h3 "More accurate"]
           [:p "20% → 53% mean score"])
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number "55% faster"]
           [:h3 "Less waiting"]
           [:p "13.6s → 6.1s per question"])
   (r/card {:variant :green :class "benchmark-stat"}
           [:span.stat-number "80% cheaper"]
           [:h3 "Fewer tokens"]
           [:p "37K → 7K input tokens"])])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "headline"} "Headline Numbers"]
   (stat-cards)

   [:h2 {:id "methodology"} "Methodology"]
   [:p
    "40 deterministic questions per repo, scored against a curated answer key. "
    "Each question is asked twice: once with raw source-file context (the "
    "“without” baseline), once via the Noumenon Ask agent (the “with” run). "
    "Same model and same evaluator across both runs. Token counts are input "
    "tokens only. Speed is wall-clock time per question."]

   [:h2 {:id "full-table"} "Per-Repository Results"]
   [:table.benchmark-table
    [:thead
     [:tr
      [:th "Repository"] [:th "Language"]
      [:th.num "Accuracy (Without → With)"]
      [:th.num "Tokens (Without → With)"]
      [:th.num "Speed (Without → With)"]]]
    (into [:tbody] (map benchmark-row benchmark-rows))]

   [:div.callout
    [:p
     "Run benchmarks yourself with " [:code "noum benchmark <repo>"]
     " or via the MCP " [:code "noumenon_benchmark_run"] " tool. "
     "Comparable metrics drop into the same Datomic graph as everything else."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Benchmarks"]
    [:p.lead
     "We evaluate Noumenon by asking deterministic questions about 8 repos in 7 "
     "languages, with and without the knowledge graph."]
    (prose-body)]])
