(ns noumenon-site.styles
  "Garden CSS for the Noumenon site. Dark theme ported from the
   hand-written style.css; structure mirrors the source file."
  (:require [garden.core :as garden]
            [garden.stylesheet :refer [at-media]]))

;; --- Design tokens ---

(def colors
  {:bg            "#0a0a0f"
   :bg-alt        "#0d1117"
   :card-bg       "#161b22"
   :border        "#1e2430"
   :border-hover  "#30363d"
   :text          "#e6edf3"
   :muted         "#8b949e"
   :muted-deep    "#6e7681"
   :blue          "#58a6ff"
   :blue-bright   "#79b8ff"
   :green         "#3fb950"
   :purple        "#d2a8ff"
   :warning       "#f0c674"})

(def font-body
  (str "-apple-system, BlinkMacSystemFont, \"Segoe UI\", "
       "\"Noto Sans\", Helvetica, Arial, sans-serif"))

(def font-mono
  (str "ui-monospace, SFMono-Regular, \"SF Mono\", Menlo, "
       "Consolas, \"Liberation Mono\", monospace"))

;; --- Reset & base ---

(def reset
  [["*, *::before, *::after" {:box-sizing "border-box"
                              :margin     0
                              :padding    0}]
   [:html {:scroll-behavior          "smooth"
           :-webkit-text-size-adjust "100%"}]
   [:body {:font-family            font-body
           :font-size              "1rem"
           :line-height            1.6
           :color                  (:text colors)
           :background             (:bg colors)
           :-webkit-font-smoothing "antialiased"}]
   [:a {:color           (:blue colors)
        :text-decoration "none"}
    [:&:hover {:text-decoration "underline"}]]
   [:img :svg {:display   "block"
               :max-width "100%"}]
   ;; Inline code anywhere: subtle box, monospace, slightly smaller.
   [:code {:font-family   font-mono
           :background    (:bg-alt colors)
           :padding       "0.1rem 0.35rem"
           :border-radius "4px"
           :font-size     "0.875em"
           :color         (:purple colors)}]
   ;; Block code: full box with scroll.
   [:pre {:background    (:bg-alt colors)
          :border        (str "1px solid " (:border colors))
          :border-radius "8px"
          :padding       "1rem 1.25rem"
          :margin        "1rem 0"
          :overflow-x    "auto"
          :font-size     "0.8125rem"
          :line-height   1.55
          :font-family   font-mono}
    [:code {:background  "none"
            :padding     0
            :color       (:text colors)
            :font-size   "0.8125rem"}]]])

;; --- Layout ---

(def layout
  [[:.container {:max-width "1200px"
                 :margin    "0 auto"
                 :padding   "0 1.5rem"}]
   [:section {:padding "5rem 0"}]
   [:.section-alt {:background (:bg-alt colors)}]
   [:.divider {:border     "none"
               :border-top (str "1px solid " (:border colors))
               :margin     0}]])

;; --- Nav ---

(def nav
  [[:.nav {:position             "sticky"
           :top                  0
           :z-index              100
           :background           "rgba(10, 10, 15, 0.92)"
           :backdrop-filter      "blur(12px)"
           :-webkit-backdrop-filter "blur(12px)"
           :border-bottom        (str "1px solid " (:border colors))}]
   [:.nav
    [:.container {:display          "flex"
                  :align-items      "center"
                  :justify-content  "space-between"
                  :height           "3.5rem"}]]
   [:.nav-brand {:font-size      "1.125rem"
                 :font-weight    600
                 :color          (:text colors)
                 :letter-spacing "-0.01em"}
    [:&:hover {:text-decoration "none"}]]
   [:.nav-links {:display    "flex"
                 :gap        "2rem"
                 :list-style "none"}
    [:a {:color      (:muted colors)
         :font-size  "0.875rem"
         :transition "color 0.15s"}
     [:&:hover {:color           (:text colors)
                :text-decoration "none"}]
     [:&.active {:color (:text colors)}]]]
   [:.nav-toggle {:display    "none"
                  :background "none"
                  :border     "none"
                  :color      (:muted colors)
                  :cursor     "pointer"
                  :padding    "0.25rem"}]])

;; --- Beta banner ---

(def beta-banner
  [[:.beta-banner {:text-align "center"
                   :padding    "0.6rem 1rem"
                   :margin-top "0.5rem"}
    [:p {:font-size "0.85rem"
         :color     (:warning colors)
         :opacity   0.9}]]])

;; --- Hero ---

(def hero
  [[:.hero {:padding    "5rem 0 4.5rem"
            :text-align "center"
            :background (str
                         "radial-gradient(ellipse 60% 50% at 50% 0%, rgba(88, 166, 255, 0.06) 0%, transparent 70%),"
                         "radial-gradient(ellipse 40% 40% at 30% 10%, rgba(63, 185, 80, 0.04) 0%, transparent 60%),"
                         "radial-gradient(ellipse 40% 40% at 70% 10%, rgba(210, 168, 255, 0.04) 0%, transparent 60%)")}]
   [:.hero-badge {:display       "inline-flex"
                  :align-items   "center"
                  :gap           "0.5rem"
                  :padding       "0.375rem 0.875rem"
                  :border-radius "999px"
                  :border        (str "1px solid " (:border colors))
                  :background    "rgba(22, 27, 34, 0.6)"
                  :font-size     "0.8125rem"
                  :color         (:muted colors)
                  :margin-bottom "1.5rem"}
    [:span {:color (:green colors)}]]
   [:.hero
    [:h1 {:font-size      "clamp(2rem, 5vw, 3.25rem)"
          :font-weight    700
          :line-height    1.15
          :letter-spacing "-0.02em"
          :margin-bottom  "1.25rem"}]]
   [:.hero-sub {:font-size   "clamp(1rem, 2.5vw, 1.1875rem)"
                :color       (:muted colors)
                :max-width   "620px"
                :margin      "0 auto 2rem"
                :line-height 1.65}]
   [:.hero-actions {:display          "flex"
                    :gap              "1rem"
                    :justify-content  "center"
                    :flex-wrap        "wrap"
                    :margin-bottom    "3rem"}]])

;; --- Buttons ---

(def buttons
  [[:.btn {:display       "inline-flex"
           :align-items   "center"
           :gap           "0.5rem"
           :padding       "0.625rem 1.5rem"
           :border-radius "6px"
           :font-size     "0.9375rem"
           :font-weight   500
           :transition    "all 0.15s"
           :border        "1px solid transparent"}]
   [:.btn-primary {:background (:blue colors)
                   :color      (:bg colors)}
    [:&:hover {:background      (:blue-bright colors)
               :text-decoration "none"}]]
   [:.btn-secondary {:background   "transparent"
                     :color        (:text colors)
                     :border-color (:border-hover colors)}
    [:&:hover {:border-color    (:muted colors)
               :text-decoration "none"}]]])

;; --- Terminal block ---

(def terminal
  [[:.terminal {:background    (:bg-alt colors)
                :border        (str "1px solid " (:border colors))
                :border-radius "8px"
                :overflow      "hidden"
                :text-align    "left"
                :max-width     "720px"
                :margin        "0 auto"}]
   [:.terminal-header {:display       "flex"
                       :align-items   "center"
                       :gap           "0.5rem"
                       :padding       "0.75rem 1rem"
                       :background    (:card-bg colors)
                       :border-bottom (str "1px solid " (:border colors))}]
   [:.terminal-dot {:width         "10px"
                    :height        "10px"
                    :border-radius "50%"
                    :background    (:border-hover colors)}]
   [:.terminal-title {:font-size   "0.75rem"
                      :color       (:muted colors)
                      :margin-left "0.5rem"}]
   [:.terminal
    [:pre {:padding     "1.25rem"
           :overflow-x  "auto"
           :font-family font-mono
           :font-size   "0.8125rem"
           :line-height 1.6
           :color       (:text colors)}]]
   [:.terminal [:.prompt    {:color (:muted colors)}]
    [:.comment   {:color (:muted-deep colors)}]
    [:.output    {:color (:muted colors)}]
    [:.highlight {:color (:blue colors)}]
    [:.green     {:color (:green colors)}]
    [:.purple    {:color (:purple colors)}]]])

;; --- Section headings ---

(def sections
  [[:.section-title {:font-size      "clamp(1.5rem, 3vw, 2rem)"
                     :font-weight    600
                     :letter-spacing "-0.01em"
                     :margin-bottom  "0.75rem"}]
   [:.section-sub {:color         (:muted colors)
                   :max-width     "600px"
                   :margin-bottom "3rem"}]
   [:.section-center {:text-align "center"}
    [:.section-sub {:margin-left "auto"
                    :margin-right "auto"}]]
   [:.eyebrow {:font-size      "0.75rem"
               :color          (:muted colors)
               :text-transform "uppercase"
               :letter-spacing "0.05em"
               :margin-bottom  "0.5rem"}]])

;; --- Cards ---

(defn- accent-card [variant rgb]
  (let [base (str "rgba(" rgb ", ")]
    [[(str ".card-" variant) {:border-color (str base "0.2)")}
      [:&:hover {:border-color (str base "0.4)")}]]
     [(str ".tag-" variant) {:color      (str "#" (case variant
                                                    "green"  "3fb950"
                                                    "purple" "d2a8ff"
                                                    "blue"   "58a6ff"))
                             :background (str base "0.1)")}]]))

(def cards
  (into
   [[:.card {:background    (:card-bg colors)
             :border        (str "1px solid " (:border colors))
             :border-radius "8px"
             :padding       "1.75rem"
             :transition    "border-color 0.15s"}
     [:&:hover {:border-color (:border-hover colors)}]
     [:h3 {:font-size     "1.0625rem"
           :font-weight   600
           :margin-bottom "0.5rem"}]
     [:p {:color       (:muted colors)
          :font-size   "0.9375rem"
          :line-height 1.55}]]
    [:.card
     [:.tag {:display        "inline-block"
             :font-size      "0.6875rem"
             :font-weight    600
             :text-transform "uppercase"
             :letter-spacing "0.05em"
             :padding        "0.1875rem 0.5rem"
             :border-radius  "4px"
             :margin-bottom  "0.75rem"}]]]
   (mapcat identity
           [(accent-card "green"  "63, 185, 80")
            (accent-card "purple" "210, 168, 255")
            (accent-card "blue"   "88, 166, 255")])))

;; --- Grids ---

(def grids
  [[:.layers-grid {:display              "grid"
                   :grid-template-columns "repeat(auto-fit, minmax(300px, 1fr))"
                   :gap                  "1.5rem"}]
   [:.features-grid {:display              "grid"
                     :grid-template-columns "repeat(auto-fit, minmax(320px, 1fr))"
                     :gap                  "1.5rem"}]
   [:.problem-grid {:display              "grid"
                    :grid-template-columns "repeat(auto-fit, minmax(280px, 1fr))"
                    :gap                  "1.5rem"}]
   ;; Inside the narrower .prose column, drop the per-card min so 3
   ;; cards fit on one row on desktop and collapse cleanly on mobile.
   [:.prose [:.layers-grid {:grid-template-columns "repeat(auto-fit, minmax(220px, 1fr))"
                            :gap                   "1rem"}]]])

;; --- Pipeline ---

(def pipeline
  [[:.pipeline {:display          "flex"
                :align-items      "center"
                :justify-content  "center"
                :gap              0
                :flex-wrap        "wrap"
                :margin-bottom    "1.5rem"}]
   [:.pipeline-step {:background    (:card-bg colors)
                     :border        (str "1px solid " (:border colors))
                     :border-radius "8px"
                     :padding       "1rem 1.5rem"
                     :text-align    "center"
                     :min-width     "120px"
                     :flex          1
                     :position      "relative"
                     :cursor        "default"}
    ["&::after" {:content       "attr(data-tip)"
                 :position      "absolute"
                 :bottom        "calc(100% + 8px)"
                 :left          "50%"
                 :transform     "translateX(-50%)"
                 :background    (:border colors)
                 :color         (:text colors)
                 :font-size     "0.75rem"
                 :line-height   1.4
                 :padding       "0.6rem 0.8rem"
                 :border-radius "6px"
                 :width         "max-content"
                 :max-width     "280px"
                 :white-space   "normal"
                 :text-align    "left"
                 :pointer-events "none"
                 :opacity       0
                 :transition    "opacity 0.15s"
                 :z-index       10
                 :box-shadow    "0 4px 12px rgba(0,0,0,0.4)"}]
    ["&:hover::after" {:opacity 1}]
    [:strong {:display       "block"
              :font-size     "0.9375rem"
              :margin-bottom "0.25rem"}]
    [:span {:font-size "0.8125rem"
            :color     (:muted colors)}]
    [:.step-note {:display    "block"
                  :font-size  "0.6875rem"
                  :color      (:green colors)
                  :margin-top "0.25rem"}]
    [:.step-note.macro    {:color (:blue colors)}]
    [:.step-note.autonomous {:color (:purple colors)}]]
   [:.pipeline-arrow {:color     (:border-hover colors)
                      :font-size "1.25rem"
                      :padding   "0 0.5rem"}]])

;; --- Steps (Get Started) ---

(def steps
  [[:.steps {:max-width "720px"
             :margin    "0 auto"
             :text-align "left"}]
   [:.step {:display       "flex"
            :gap           "1.25rem"
            :margin-bottom "2.25rem"}
    [:&:last-child {:margin-bottom 0}]]
   [:.step-number {:flex-shrink     0
                   :width           "2rem"
                   :height          "2rem"
                   :background      "rgba(88, 166, 255, 0.1)"
                   :color           (:blue colors)
                   :border-radius   "50%"
                   :display         "flex"
                   :align-items     "center"
                   :justify-content "center"
                   :font-size       "0.875rem"
                   :font-weight     600
                   :margin-top      "0.1rem"}]
   [:.step-content {:min-width 0}
    [:h3 {:font-size     "1rem"
          :font-weight   600
          :line-height   "2rem"
          :margin-bottom "0.5rem"}]
    [:p {:color         (:muted colors)
         :font-size     "0.9375rem"
         :margin-bottom "0.75rem"}]
    [:code {:display       "block"
            :background    (:bg-alt colors)
            :border        (str "1px solid " (:border colors))
            :border-radius "6px"
            :padding       "0.75rem 1rem"
            :font-size     "0.8125rem"
            :color         (:text colors)
            :overflow-x    "auto"}]
    [:.step-hint {:margin-top "0.5rem"
                  :font-size  "0.85rem"
                  :color      (:muted colors)}]]])

;; --- Showcase (split content + terminal) ---

(def showcase
  [[:.showcase {:display              "grid"
                :grid-template-columns "1fr 1fr"
                :gap                  "2rem"
                :align-items          "start"}
    [:.showcase-text
     [:h3 {:font-size     "1.125rem"
           :font-weight   600
           :margin-bottom "0.75rem"}]
     [:p {:color         (:muted colors)
          :font-size     "0.9375rem"
          :line-height   1.6
          :margin-bottom "1rem"}]]]])

;; --- Benchmark stats + table ---

(def benchmarks
  [[:.benchmark-stat {:text-align "center"}
    [:.stat-number {:font-size   "2rem"
                    :font-weight 700
                    :color       (:green colors)}]]])

;; --- Footer ---

(def footer
  [[:.footer {:border-top (str "1px solid " (:border colors))
              :padding    "3rem 0 2rem"
              :color      (:muted colors)
              :font-size  "0.8125rem"}]
   [:.footer
    [:.container {:display "block"}]]
   [:.footer-grid {:display               "grid"
                   :grid-template-columns "repeat(4, 1fr)"
                   :gap                   "2rem"
                   :margin-bottom         "2rem"}]
   [:.footer-col
    [:h4 {:color         (:text colors)
          :font-size     "0.8125rem"
          :font-weight   600
          :margin-bottom "0.75rem"
          :text-transform "uppercase"
          :letter-spacing "0.04em"}]
    [:ul {:list-style "none"
          :display    "flex"
          :flex-direction "column"
          :gap        "0.4rem"}]
    [:a {:color     (:muted colors)
         :font-size "0.8125rem"}
     [:&:hover {:color (:text colors)}]]]
   [:.footer-credit {:text-align "center"
                     :padding-top "1.5rem"
                     :border-top (str "1px solid " (:border colors))}]])

;; --- Doc / hub layouts (added in IA redesign) ---

(def hub
  [[:.lead {:font-size "1.05rem"
            :color     (:muted colors)
            :max-width "62ch"
            :margin    "0.75rem 0 2rem"
            :line-height 1.55}]
   [:.hub-grid {:display               "grid"
                :grid-template-columns "repeat(auto-fit, minmax(260px, 1fr))"
                :gap                   "1.25rem"
                :margin-top            "2rem"}]
   [:.hub-card {:display     "block"
                :padding     "1.5rem"
                :background  (:card-bg colors)
                :border      (str "1px solid " (:border colors))
                :border-radius "10px"
                :color       (:text colors)
                :transition  "border-color 0.15s, transform 0.15s"}
    [:&:hover {:border-color    (:border-hover colors)
               :text-decoration "none"
               :transform       "translateY(-1px)"}]
    [:h3 {:font-size     "1.05rem"
          :margin-bottom "0.5rem"}]
    [:p {:color     (:muted colors)
         :font-size "0.9rem"
         :margin    0}]]])

(def prose
  [[:section.docs {:padding "3rem 0 4rem"}]
   [:.docs-title {:font-size     "1.875rem"
                  :margin-bottom "0.75rem"
                  :line-height   1.2}]
   [:.prose {:max-width "820px"}
    [:h2 {:font-size      "1.35rem"
          :margin         "2.25rem 0 0.85rem"
          :padding-bottom "0.4rem"
          :border-bottom  (str "1px solid " (:border colors))}]
    [:h3 {:font-size "1.05rem"
          :margin    "1.5rem 0 0.5rem"
          :color     (:text colors)}]
    [:p {:margin    "0.65rem 0"
         :font-size "0.95rem"
         :max-width "72ch"
         :color     (:text colors)}]
    [:ul {:margin    "0.65rem 0 0.65rem 1.5rem"
          :max-width "72ch"
          :font-size "0.95rem"}]
    [:li {:margin-bottom "0.35rem"
          :color         (:text colors)}]
    [:a {:color (:blue colors)}]
    [:strong {:color (:text colors)}]]])

(def tables
  [[".md-table, .benchmark-table, .queries-table"
    {:width           "100%"
     :border-collapse "collapse"
     :margin          "1.5rem 0"
     :font-size       "0.9rem"
     :border          (str "1px solid " (:border colors))
     :border-radius   "6px"
     :overflow        "hidden"}]
   [".md-table th, .md-table td, .benchmark-table th, .benchmark-table td, .queries-table th, .queries-table td"
    {:padding        "0.6rem 0.85rem"
     :text-align     "left"
     :vertical-align "top"
     :border-bottom  (str "1px solid " (:border colors))}]
   [".md-table th, .benchmark-table th, .queries-table th"
    {:color           (:text colors)
     :font-weight     600
     :background      (:bg-alt colors)
     :font-size       "0.78rem"
     :text-transform  "uppercase"
     :letter-spacing  "0.04em"}]
   [".md-table td, .queries-table td" {:color (:muted colors)}]
   [".md-table tbody tr:last-child td, .benchmark-table tbody tr:last-child td, .queries-table tbody tr:last-child td"
    {:border-bottom "none"}]
   [".md-table td code, .queries-table td code"
    {:font-size "0.85em"}]
   [".benchmark-table td.num, .benchmark-table th.num"
    {:text-align "right"
     :font-variant-numeric "tabular-nums"}]
   [".md-table tbody tr:hover, .benchmark-table tbody tr:hover, .queries-table tbody tr:hover"
    {:background "rgba(99, 102, 241, 0.04)"}]])

(def highlight-css
  [[:.hl-keyword {:color (:blue-bright colors)}]
   [:.hl-string  {:color (:green colors)}]
   [:.hl-number  {:color (:warning colors)}]
   [:.hl-comment {:color      (:muted-deep colors)
                  :font-style "italic"}]
   [:.hl-type    {:color (:purple colors)}]])

(def queries-css
  [[:.queries-filter {:display       "block"
                      :width         "100%"
                      :max-width     "420px"
                      :margin        "1.25rem 0 1rem"
                      :padding       "0.55rem 0.85rem"
                      :background    (:bg-alt colors)
                      :border        (str "1px solid " (:border colors))
                      :border-radius "6px"
                      :color         (:text colors)
                      :font-family   font-body
                      :font-size     "0.95rem"}
    [:&:focus {:outline      "none"
               :border-color (:blue colors)}]]
   [:.queries-table
    [:.query-name {:color       (:text colors)
                   :font-family font-mono
                   :font-size   "0.85rem"}]
    [:.query-inputs {:font-family font-mono
                     :font-size   "0.8rem"
                     :color       (:purple colors)
                     :background  "transparent"
                     :padding     0}]]])

(def mermaid-frame
  [[:.mermaid-frame {:padding       "1.5rem"
                     :margin        "1.5rem 0 2rem"
                     :background    (:bg-alt colors)
                     :border        (str "1px solid " (:border colors))
                     :border-radius "10px"
                     :overflow-x    "auto"}]
   [:.mermaid-frame [:.mermaid {:background "transparent"
                                :font-size  "0.9rem"}]]])

(def schema-table
  [[:.schema-table {:width           "100%"
                    :border-collapse "collapse"
                    :margin          "0.75rem 0 1.5rem"
                    :font-size       "0.875rem"
                    :border          (str "1px solid " (:border colors))
                    :border-radius   "6px"
                    :overflow        "hidden"}
    [:th :td {:padding        "0.5rem 0.75rem"
              :text-align     "left"
              :vertical-align "top"
              :border-bottom  (str "1px solid " (:border colors))}]
    [:th {:color           (:text colors)
          :font-weight     600
          :background      (:bg-alt colors)
          :font-size       "0.78rem"
          :text-transform  "uppercase"
          :letter-spacing  "0.04em"}]
    [:td {:color (:muted colors)}
     [:code {:font-size "0.8em"}]]
    [:tbody [:tr [:&:hover {:background "rgba(99, 102, 241, 0.04)"}]]]]])

(def http-method
  [[:.http-method {:display       "inline-block"
                   :font-family   font-mono
                   :font-size     "0.7rem"
                   :font-weight   700
                   :letter-spacing "0.05em"
                   :padding       "0.15rem 0.45rem"
                   :border-radius "4px"
                   :min-width     "3.5rem"
                   :text-align    "center"
                   :color         (:text colors)
                   :background    (:bg-alt colors)}]
   [:.http-get    {:color "#3fb950" :background "rgba(63,185,80,0.12)"}]
   [:.http-post   {:color "#58a6ff" :background "rgba(88,166,255,0.12)"}]
   [:.http-put    {:color "#f0c674" :background "rgba(240,198,116,0.12)"}]
   [:.http-patch  {:color "#d2a8ff" :background "rgba(210,168,255,0.12)"}]
   [:.http-delete {:color "#ff7b72" :background "rgba(255,123,114,0.12)"}]])

(def callout
  [[:.callout {:padding       "1rem 1.25rem"
               :margin        "1.25rem 0"
               :background    (:bg-alt colors)
               :border-left   (str "3px solid " (:blue colors))
               :border-radius "6px"
               :color         (:text colors)}
    [:p {:margin     0
         :font-size  "0.95rem"}]]])

(def toc
  [[:.toc {:padding       "1rem 1.25rem"
           :margin        "1rem 0 2rem"
           :background    (:bg-alt colors)
           :border        (str "1px solid " (:border colors))
           :border-radius "6px"
           :font-size     "0.9rem"}
    [:h4 {:font-size     "0.75rem"
          :text-transform "uppercase"
          :letter-spacing "0.05em"
          :color         (:muted colors)
          :margin-bottom "0.5rem"}]
    [:ul {:list-style "none"
          :display    "flex"
          :flex-direction "column"
          :gap        "0.3rem"}]
    [:a {:color (:muted colors)}
     [:&:hover {:color (:text colors)}]]]])

;; --- Mobile ---

(def mobile
  [(at-media {:max-width "768px"}
             [:section {:padding "3.5rem 0"}]
             [:.hero {:padding "4rem 0 3.5rem"}]
             [:.nav-links {:display          "none"
                           :position         "absolute"
                           :top              "3.5rem"
                           :left             0
                           :right            0
                           :flex-direction   "column"
                           :background       "rgba(10, 10, 15, 0.98)"
                           :border-bottom    (str "1px solid " (:border colors))
                           :padding          "1rem 1.5rem"
                           :gap              "0.75rem"}]
             [:.nav-links.open {:display "flex"}]
             [:.nav-toggle {:display "block"}]
             [:.showcase {:grid-template-columns "1fr"}]
             [:.pipeline {:flex-direction "column"}]
             [:.pipeline-arrow {:transform "rotate(90deg)"
                                :padding   "0.25rem 0"}]
             [:.benchmark-table {:font-size "0.85rem"}
              [:th :td {:padding "0.5rem 0.6rem"}]])])

;; --- Compose ---

(def stylesheet
  (concat reset layout nav beta-banner hero buttons terminal
          sections cards grids pipeline steps showcase
          benchmarks footer
          hub prose tables highlight-css queries-css http-method
          mermaid-frame schema-table callout toc
          mobile))

(defn render
  "Compile the stylesheet into a CSS string. Inlined into <style>."
  []
  (garden/css stylesheet))
