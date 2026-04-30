(ns noumenon-site.content.concepts.introspect
  "Introspect concept page — autonomous loop, five targets, iteration anatomy."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(def introspect-terminal-body
  [[:span.prompt "$"] " noum introspect ./my-repo --max-iterations 5 --provider glm\n\n"
   [:span.comment "# baseline mean=52.3% (22 deterministic questions)"] "\n"
   [:span.comment "# === Iteration 1/5 ==="] "\n"
   [:span.comment "# target=system-prompt: \"Fix empty result handling\""] "\n"
   [:span.comment "# IMPROVED +6.8% (52.3% -> 59.1%)"] "\n"
   [:span.comment "# === Iteration 2/5 ==="] "\n"
   [:span.comment "# target=examples: \"Add dependency query patterns\""] "\n"
   [:span.comment "# reverted (delta=-4.5%)"] "\n"
   [:span.comment "# === Iteration 3/5 ==="] "\n"
   [:span.comment "# target=examples: \"Replace low-impact examples\""] "\n"
   [:span.comment "# IMPROVED +11.4% (56.8% -> 68.2%)"] "\n\n"
   "Introspect complete: 2 improvements in 3 iterations (final score: 68.2%)"])

(def targets
  [["system-prompt" "Edit the prompt that drives the Ask agent."]
   ["examples"      "Add, remove, or replace few-shot examples shown to the model."]
   ["rules"         "Adjust derived rules used during query planning."]
   ["source-code"   "Patch a small region of the Ask agent itself."]
   ["model-config"  "Swap providers, models, or sampling parameters."]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "loop"} "The autonomous loop"]
   [:p
    "Each iteration picks one of five targets, drafts a change, applies it, runs "
    "the benchmark suite, and compares the new score against the baseline. The loop "
    "is bounded by iterations, wall-clock time, or LLM cost, whichever you set first."]

   [:h2 {:id "targets"} "Five targets"]
   [:table.md-table
    [:thead [:tr [:th "Target"] [:th "What changes"]]]
    (into [:tbody]
          (for [[k v] targets]
            [:tr [:td [:code k]] [:td v]]))]

   [:h2 {:id "anatomy"} "Anatomy of an iteration"]
   [:p
    "A run prints its baseline, then for each iteration: the chosen target, the "
    "proposed change, the resulting score, and an "
    [:strong "IMPROVED"] " or " [:strong "reverted"] " verdict."]
   [:div {:style "margin: 1.25rem 0;"}
    (apply r/terminal introspect-terminal-body)]

   [:div.callout
    [:p
     "Run " [:code "noum introspect <repo> --help"] " for the full flag set, "
     "or invoke the MCP equivalents: " [:code "noumenon_introspect"]
     ", " [:code "noumenon_introspect_start"] ", and "
     [:code "noumenon_introspect_status"] "."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Introspect"]
    [:p.lead
     "Noumenon improves itself by running an autonomous loop. Propose a change, "
     "benchmark it against a fixed question set, keep the change if quality goes up, "
     "revert it if not."]
    (prose-body)]])
