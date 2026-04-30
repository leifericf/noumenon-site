(ns noumenon-site.styles
  "Garden CSS for the Noumenon site. Minimal Phase-1 styling — just
   enough that the page reads well. Phase 2 redesign drives polish."
  (:require [garden.core :as garden]
            [garden.stylesheet :refer [at-media]]))

(def colors
  {:bg          "#ffffff"
   :bg-subtle   "#f8f9fa"
   :text        "#2d3436"
   :text-muted  "#808b96"
   :heading     "#1a1a2e"
   :link        "#2c5282"
   :link-hover  "#1a365d"
   :border      "#e8ecf0"
   :code-bg     "#f5f7fa"
   :code-text   "#2d3436"})

(def font-body
  (str "-apple-system, BlinkMacSystemFont, 'Segoe UI', "
       "Roboto, Oxygen, Ubuntu, Cantarell, sans-serif"))

(def font-mono
  (str "'SF Mono', 'Cascadia Code', 'JetBrains Mono', "
       "'Fira Code', Menlo, Consolas, monospace"))

(def stylesheet
  [[:* {:box-sizing "border-box"}]
   [:html {:font-size "16px"}]
   [:body {:margin             0
           :font-family        font-body
           :color              (:text colors)
           :background         (:bg colors)
           :line-height        1.6
           :-webkit-font-smoothing "antialiased"}]
   [:a {:color (:link colors)
        :text-decoration "none"}
    [:&:hover {:color           (:link-hover colors)
               :text-decoration "underline"}]]
   [:h1 :h2 :h3 :h4 {:color       (:heading colors)
                     :line-height 1.25
                     :margin      "1.5rem 0 0.75rem"}]
   [:h1 {:font-size "2.25rem"
         :margin    "0 0 1rem"}]
   [:h2 {:font-size "1.5rem"}]
   [:h3 {:font-size "1.15rem"}]
   [:p {:margin "0 0 1rem"}]
   [:code {:font-family font-mono
           :font-size   "0.95em"
           :background  (:code-bg colors)
           :padding     "0.1em 0.35em"
           :border-radius "3px"}]
   [:pre {:background  (:code-bg colors)
          :color       (:code-text colors)
          :padding     "1rem 1.25rem"
          :border-radius "6px"
          :overflow-x  "auto"
          :line-height 1.5}
    [:code {:background  "transparent"
            :padding     0
            :font-size   "0.9rem"}]]
   [:hr {:border       "none"
         :border-top   (str "1px solid " (:border colors))
         :margin       "2.5rem 0"}]

   ;; Layout
   [:.container {:max-width "780px"
                 :margin    "0 auto"
                 :padding   "2rem 1.25rem 4rem"}]

   ;; Header
   [:.site-header {:border-bottom (str "1px solid " (:border colors))
                   :background    (:bg colors)
                   :padding       "1rem 0"}]
   [:.site-nav {:display          "flex"
                :align-items      "center"
                :justify-content  "space-between"
                :max-width        "780px"
                :margin           "0 auto"
                :padding          "0 1.25rem"}]
   [:.site-nav-brand {:font-weight 600
                      :color       (:heading colors)
                      :font-size   "1.05rem"}]
   [:.site-nav-links {:display          "flex"
                      :gap              "1.5rem"
                      :list-style       "none"
                      :padding          0
                      :margin           0
                      :font-size        "0.95rem"}]

   ;; Hero
   [:.hero {:padding-top "2.5rem"}]
   [:.hero-tagline {:font-size  "1.15rem"
                    :color      (:text-muted colors)
                    :max-width  "60ch"}]

   ;; Section
   [:.section {:margin-top "2.5rem"}]
   [:.section-title {:font-size "1.4rem"}]

   ;; Footer
   [:.site-footer {:border-top  (str "1px solid " (:border colors))
                   :margin-top  "4rem"
                   :padding     "2rem 0"
                   :color       (:text-muted colors)
                   :font-size   "0.9rem"
                   :text-align  "center"}]

   (at-media {:max-width "640px"}
             [:h1 {:font-size "1.75rem"}]
             [:.site-nav {:flex-direction "column"
                          :gap            "0.5rem"}])])

(defn render
  "Compile the stylesheet into a CSS string. Inlined into <style>."
  []
  (garden/css stylesheet))
