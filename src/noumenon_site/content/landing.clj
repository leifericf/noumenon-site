(ns noumenon-site.content.landing
  "Landing page: pitch and signposts. The bulk of detail moved to
   /concepts/, /reference/, and /server/ in the IA redesign."
  (:require [hiccup2.core :as h]
            [noumenon-site.render :as r]))

(defn- section
  [{:keys [id alt centered]} & body]
  (cond-> [:section]
    id       (conj {:id id})
    alt      (update-in [1] assoc :class "section-alt")
    true     (conj (into [:div {:class (cond-> "container"
                                         centered (str " section-center"))}]
                         body))))

(def hero-terminal-body
  [[:span.prompt "$"] " noum ask ./my-repo "
   [:span.green "\"Which files are the biggest risk hotspots?\""]
   "\n\n"
   [:span.comment "# iteration 1: querying files-by-complexity (47 results)"] "\n"
   [:span.comment "# iteration 2: querying commit frequency and authorship"] "\n"
   [:span.comment "# iteration 3: cross-referencing co-change patterns"] "\n\n"
   [:span.highlight "Top 5 risk hotspots:"] "\n"
   [:span.output "  src/api/middleware.ts    38 commits, 4 authors, very-complex\n"
    "  src/core/parser.ts       47 commits, 2 authors, very-complex\n"
    "  src/db/migrations.ts     31 commits, 6 authors, complex\n"
    "  src/auth/session.ts      28 commits, 3 authors, complex\n"
    "  src/cli/commands.ts      24 commits, 5 authors, complex"]])

(defn- hero []
  [:section.hero
   [:div.container
    [:div.hero-badge
     [:span (h/raw "&#9679;")]
     " Open source · Self-contained · MIT License"]
    [:h1 (h/raw "Precise, grounded answers<br>about your codebase.")]
    [:p.hero-sub
     "Compile your repository into a "
     [:a {:href "https://www.datomic.com"} "Datomic"]
     " knowledge graph. AI agents query structured facts instead of scanning raw source. "
     [:strong "2.7× more accurate"] ", "
     [:strong "80% cheaper"] ", and "
     [:strong "55% faster"] " across 8 repos."]
    [:div.hero-actions
     [:a.btn.btn-primary   {:href "/get-started/"} "Get started"]
     [:a.btn.btn-secondary {:href "https://github.com/leifericf/noumenon"} "View on GitHub"]]
    (apply r/terminal hero-terminal-body)]])

(def compressed-pipeline
  [{:title "Import"     :subtitle "Git history & files"  :note "deterministic"}
   {:title "Enrich"     :subtitle "Import graph"         :note "deterministic"}
   {:title "Analyze"    :subtitle "Per-file semantics"   :note "micro / LLM"}
   {:title "Synthesize" :subtitle "Components & arch"    :note "macro / LLM" :note-class :macro}
   {:title "Embed"      :subtitle "Vector search index"  :note "deterministic"}])

(defn- pipeline-glance []
  (section {:id "pipeline-glance" :alt true :centered true}
           [:h2.section-title "Five stages, then queries"]
           [:p.section-sub
            "Each stage builds on the last. "
            [:a {:href "/concepts/pipeline/"} "Read the full pipeline →"]]
           (into [:div.pipeline]
                 (interpose [:span.pipeline-arrow (h/raw "&rarr;")]
                            (map r/pipeline-step compressed-pipeline)))))

(defn- benchmark-stats []
  (section {:id "stats" :centered true}
           [:h2.section-title "Measured on real codebases"]
           [:p.section-sub
            "40 questions per repo, 8 repos, 7 languages. "
            [:a {:href "/concepts/benchmarks/"} "See the full table →"]]
           [:div.layers-grid
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
                    [:p "37K → 7K input tokens"])]))

(defn- step [n title code hint]
  [:div.step
   [:div.step-number n]
   [:div.step-content
    [:h3 title]
    [:code code]
    (when hint [:p.step-hint hint])]])

(defn- thirty-seconds []
  (section {:id "thirty-seconds" :alt true :centered true}
           [:h2.section-title "30 seconds to start"]
           [:div.steps
            (step 1 "Install"
                  "curl -sSL https://noumenon.leifericf.com/install | bash"
                  [:span "Also via " [:a {:href "/get-started/"} "Homebrew, Scoop, Docker"] "."])
            (step 2 "Run the demo"
                  "noum demo"
                  "Pre-built knowledge graph. No LLM credentials needed.")
            (step 3 "Ask"
                  "noum ask noumenon \"Which files are the biggest risk hotspots?\""
                  nil)]))

(defn- explore []
  (section {:id "explore" :centered true}
           [:h2.section-title "Where to next"]
           [:div.hub-grid
            [:a.hub-card {:href "/get-started/"}
             [:h3 "Install"]
             [:p "Quickstart, package managers, and LLM provider setup."]]
            [:a.hub-card {:href "/concepts/"}
             [:h3 "How it works"]
             [:p "Knowledge graph levels, the pipeline, introspection, and benchmarks."]]
            [:a.hub-card {:href "/queries/"}
             [:h3 "Query catalog"]
             [:p "Browse 90+ named Datalog queries shipped with Noumenon."]]
            [:a.hub-card {:href "/mcp/"}
             [:h3 "MCP integration"]
             [:p "Use Noumenon from Claude Desktop, Claude Code, or any MCP client."]]]))

(defn landing-page []
  [:div
   (hero)
   [:hr.divider]
   (pipeline-glance)
   [:hr.divider]
   (benchmark-stats)
   [:hr.divider]
   (thirty-seconds)
   [:hr.divider]
   (explore)])
