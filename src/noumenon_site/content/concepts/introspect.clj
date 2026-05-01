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
  [["system-prompt" "Edit the system prompt that drives the Ask agent."]
   ["examples"      "Add, remove, or replace few-shot examples shown to the model."]
   ["rules"         "Adjust derived rules used during query planning."]
   ["code"          "Patch a small region of the Ask agent's source code."]
   ["train"         "Retrain the routing model that picks which files and queries to seed before the agent runs."]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "loop"} "The Autonomous Loop"]
   [:p
    "Each iteration picks one of five targets, drafts a change, applies it, runs "
    "the benchmark suite, and compares the new score against the baseline. The loop "
    "is bounded by iterations, wall-clock time, or LLM cost, whichever you set first."]

   [:h2 {:id "targets"} "Five Targets"]
   [:table.md-table
    [:thead [:tr [:th "Target"] [:th "What changes"]]]
    (into [:tbody]
          (for [[k v] targets]
            [:tr [:td [:code k]] [:td v]]))]

   [:h2 {:id "anatomy"} "Anatomy of an Iteration"]
   [:p
    "A run prints its baseline, then for each iteration: the chosen target, the "
    "proposed change, the resulting score, and an "
    [:strong "IMPROVED"] " or " [:strong "reverted"] " verdict."]
   [:div {:style "margin: 1.25rem 0;"}
    (apply r/terminal introspect-terminal-body)]

   [:h2 {:id "signals"} "Signals from Ask Sessions"]
   [:p
    "Benchmarks are not the only input. The optimizer's meta-prompt "
    "also includes a " [:code "{{ask-insights}}"]
    " block aggregated from real Ask sessions in the meta database. "
    "Three streams feed it:"]
   [:ul
    [:li
     [:strong "Agent self-reflection. "]
     "When the " [:a {:href "/concepts/ask/#reflect"} "Ask agent"]
     " emits " [:code ":missing-attributes"] ", "
     [:code ":quality-issues"] ", and " [:code ":suggested-queries"]
     " on a session, those are aggregated by frequency. \"Function "
     "dependencies (reported 10×)\" becomes a signal to add a schema "
     "attribute. \"Empty commit messages (reported 7×)\" signals a "
     "data-quality issue."]
    [:li
     [:strong "Telemetry queries. "]
     [:code "ask-empty-results"] " surfaces queries that returned "
     "nothing — gaps in the data model. " [:code "ask-popular-queries"]
     " surfaces patterns the LLM writes most often — candidates for "
     "named queries with optimized descriptions in the system prompt. "
     [:code "ask-error-steps"] " surfaces parse failures — signals "
     "for prompt improvements."]
    [:li
     [:strong "Explicit feedback. "]
     "Thumbs-up/thumbs-down on a past session is recorded on "
     [:code ":ask.session/feedback"]
     ". Negative feedback is surfaced first in the meta-prompt."]]
   [:p
    "The result is a closed loop: agents try to answer questions, "
    "tell the system what's missing or broken, and introspect "
    "proposes the fix on the next run. Real questions are a higher-"
    "quality signal than synthetic benchmarks because they reflect "
    "what people actually need."]

   [:div.callout
    [:p
     "Run " [:code "noum introspect <repo> --help"] " for the full flag set, "
     "or invoke the MCP equivalents: " [:code "noumenon_introspect"]
     ", " [:code "noumenon_introspect_start"] ", and "
     [:code "noumenon_introspect_status"] ". The Ask agent's side of "
     "the loop lives at "
     [:a {:href "/concepts/ask/"} "Ask"] "."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Introspect"]
    [:p.lead
     "Noumenon improves itself by running an autonomous loop. Propose a change, "
     "benchmark it against a fixed question set, keep the change if quality goes up, "
     "revert it if not."]
    (prose-body)]])
