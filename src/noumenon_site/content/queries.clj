(ns noumenon-site.content.queries
  "Queries catalog page: grouped by category with a sticky sidebar."
  (:require [clojure.string :as str]
            [noumenon-site.parse.queries :as parse]
            [noumenon-site.render :as render]))

(def ^:private categories
  ;; [Heading, slug, predicate-on-name].
  ;; Order is the order they appear on the page and in the sidebar.
  [["Hotspots and Risk"        "hotspots"
    #(or (#{"hotspots" "bug-hotspots" "complex-hotspots"} %)
         (str/includes? % "smells")
         (str/includes? % "tech-debt")
         (str/includes? % "churn-by"))]
   ["Files"                    "files"
    #(or (str/starts-with? % "files-by")
         (str/starts-with? % "file-")
         (= % "orphan-files"))]
   ["Code Segments"            "segments"
    #(or (str/includes? % "segments")
         (str/includes? % "segment-")
         (= % "ai-authored-segments")
         (= % "deprecated-but-active"))]
   ["Components and Architecture" "components"
    #(or (str/starts-with? % "component-")
         (#{"components" "subsystems" "cross-component-imports"} %))]
   ["Imports and Dependencies" "deps"
    #(or (= % "all-import-edges")
         (str/includes? % "imports")
         (str/includes? % "dependency")
         (str/includes? % "boundary")
         (str/includes? % "test-impact")
         (str/includes? % "transitive")
         (str/includes? % "shared-dependencies")
         (str/includes? % "circular"))]
   ["Authors and Contributors" "authors"
    #(or (str/starts-with? % "author")
         (str/starts-with? % "authors-")
         (str/starts-with? % "bus-factor")
         (#{"top-contributors" "fix-authors"} %))]
   ["Git History"              "git"
    #(or (str/starts-with? % "commit")
         (= % "recent-commits")
         (= % "issue-refs")
         (= % "commits-by-issue"))]
   ["Ask Sessions"             "ask"
    #(str/starts-with? % "ask-")]
   ["Benchmarks"               "bench"
    #(str/starts-with? % "benchmark-")]
   ["Introspect"               "introspect"
    #(str/starts-with? % "introspect-")]
   ["LLM Cost"                 "cost"
    #(str/starts-with? % "llm-cost-")]
   ["Catalog and Rules"        "meta"
    #{"index" "rules"}]])

(defn- assign-category [{:keys [name]}]
  (or (some (fn [[heading slug pred]]
              (when (pred name) [heading slug]))
            categories)
      ["Other" "other"]))

(defn- group-queries [queries]
  (let [by-cat (group-by assign-category queries)]
    (for [[heading slug _] (conj (vec categories) ["Other" "other" nil])
          :let [items (get by-cat [heading slug])]
          :when (seq items)]
      {:heading heading
       :slug    slug
       :queries (sort-by :name items)})))

(defn- query-row [{:keys [name description inputs]}]
  (let [inputs-str (when (seq inputs)
                     (str/join ", " (map (comp str symbol) inputs)))]
    [:tr {:data-name name :data-desc (or description "")}
     [:td [:span.query-name name]]
     [:td description]
     [:td (when inputs-str [:span.query-inputs inputs-str])]]))

(defn- category-section [{:keys [heading slug queries]}]
  [:div.list-section {:id (str "cat-" slug)}
   [:h3 (str heading " (" (count queries) ")")]
   [:table.queries-table
    [:thead
     [:tr [:th "Name"] [:th "Description"] [:th "Inputs"]]]
    (into [:tbody] (map query-row queries))]])

(defn- sidebar [groups total]
  (render/sidebar-nav
   [{:heading (str total " queries")
     :items (for [{:keys [heading slug queries]} groups]
              {:href            (str "#cat-" slug)
               :data-section-id (str "cat-" slug)
               :label           (str heading " · " (count queries))})}]))

(defn page []
  (let [queries (parse/all)
        groups  (group-queries queries)]
    [:section.docs
     [:div.container-wide
      [:h1.docs-title "Query Catalog"]
      [:p.lead
       (count queries) " named Datalog queries shipped with Noumenon, "
       "grouped by purpose. Run with " [:code "noum query <name> [args...]"]
       " or invoke via MCP " [:code "noumenon_query"] "."]
      [:div.docs-layout
       (sidebar groups (count queries))
       [:div.docs-content
        [:input.queries-filter
         {:id          "queries-filter"
          :type        "search"
          :placeholder "Filter queries by name or description..."
          :aria-label  "Filter queries"}]
        (into [:div.prose] (map category-section groups))
        [:div.callout {:style "margin-top: 2rem;"}
         [:p
          "Source-of-truth lives at "
          [:a {:href "https://github.com/leifericf/noumenon/tree/main/resources/queries"}
           "resources/queries/"]
          " in the Noumenon repo. Submit a PR to add a query, then it shows up here on the next site rebuild."]]]]]]))
