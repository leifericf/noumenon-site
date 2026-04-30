(ns noumenon-site.render
  "Page chrome and shared building blocks (head, nav, footer, terminal, cards)."
  (:require [hiccup2.core :as h]
            [noumenon-site.styles :as styles]))

;; --- Site config ---

(def site-title "Noumenon")
(def site-description
  "Compile your repo into a knowledge graph. Get grounded answers instead of hallucinations.")
(def site-og-description
  "Compiles git repos into a Datomic knowledge graph so AI agents answer codebase questions more accurately, faster, and cheaper than raw context windows.")
(def site-url "https://noumenon.leifericf.com")
(def site-tagline
  "Noumenon - Precise, Grounded Answers About Your Codebase")

(def nav-items
  [{:href "#layers"        :label "Knowledge Levels"}
   {:href "#how-it-works"  :label "How It Works"}
   {:href "#introspect"    :label "Introspect"}
   {:href "#benchmarks"    :label "Benchmarks"}
   {:href "#features"      :label "Features"}
   {:href "#server-mode"   :label "Server Mode"}
   {:href "#get-started"   :label "Get Started"}
   {:href "https://github.com/leifericf/noumenon" :label "GitHub"}])

(def beta-notice
  "Experimental, early beta — data model and interfaces are unstable. Expect breaking changes between releases.")

;; --- Head ---

(defn- head [{:keys [title description]}]
  (let [page-title (or title site-tagline)
        desc       (or description site-description)]
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:title page-title]
     [:meta {:name "description" :content desc}]
     [:meta {:property "og:title"       :content page-title}]
     [:meta {:property "og:description" :content site-og-description}]
     [:meta {:property "og:url"         :content site-url}]
     [:meta {:property "og:type"        :content "website"}]
     [:meta {:name "twitter:card"       :content "summary_large_image"}]
     [:link {:rel "icon" :href "/favicon.svg" :type "image/svg+xml"}]
     [:style (h/raw (styles/render))]]))

;; --- Nav ---

(defn- nav-toggle-icon []
  [:svg {:width 20 :height 20 :viewBox "0 0 20 20" :fill "currentColor"}
   [:rect {:y  3 :width 20 :height 2 :rx 1}]
   [:rect {:y  9 :width 20 :height 2 :rx 1}]
   [:rect {:y 15 :width 20 :height 2 :rx 1}]])

(defn- nav []
  [:nav.nav
   [:div.container
    [:a.nav-brand {:href "#"} site-title]
    [:button.nav-toggle {:aria-label "Menu"} (nav-toggle-icon)]
    [:ul.nav-links
     (for [{:keys [href label]} nav-items]
       [:li [:a {:href href} label]])]]])

(defn- beta-banner []
  [:div.beta-banner.container
   [:p (h/raw beta-notice)]])

;; --- Footer ---

(def footer-links
  [{:href "https://github.com/leifericf/noumenon"                      :label "GitHub"}
   {:href "https://github.com/leifericf/noumenon#readme"               :label "Documentation"}
   {:href "https://github.com/leifericf/noumenon/blob/main/CHANGES.md" :label "Changelog"}
   {:href "https://github.com/leifericf/noumenon/blob/main/LICENSE"    :label "MIT License"}
   {:href "https://www.datomic.com"                                    :label "Datomic"}
   {:href "https://github.com/leifericf/claude-code-toolkit"           :label "Claude Code Toolkit"}])

(defn- footer []
  [:footer.footer
   [:div.container
    [:div.footer-links
     (for [{:keys [href label]} footer-links]
       [:a {:href href} label])]
    [:p "Leif Eric Fredheim"]]])

;; --- Mobile nav script ---

(def mobile-nav-script
  "document.querySelector('.nav-toggle').addEventListener('click',function(){
   document.querySelector('.nav-links').classList.toggle('open');
 });
 document.querySelectorAll('.nav-links a').forEach(function(link){
   link.addEventListener('click',function(){
     document.querySelector('.nav-links').classList.remove('open');
   });
 });")

;; --- Building blocks (used by content/landing) ---

(defn terminal
  "Wraps body in a styled terminal block (header dots + <pre>).
   body is the Hiccup contents that go inside <pre>."
  [& body]
  [:div.terminal
   [:div.terminal-header
    [:span.terminal-dot]
    [:span.terminal-dot]
    [:span.terminal-dot]
    [:span.terminal-title "Terminal"]]
   (into [:pre] body)])

(defn card
  "Card with optional accent variant (:green | :purple | :blue).
   opts:
     :variant — accent color
     :tag     — optional tag label (uses matching tag-{variant})
     :title   — h3 text
     :class   — extra classes (string)"
  [{:keys [variant tag title class]} & body]
  (let [class-name (cond-> "card"
                     variant      (str " card-" (name variant))
                     class        (str " " class))]
    (cond-> [:div {:class class-name}]
      tag         (conj [:span {:class (str "tag tag-" (name variant))} tag])
      title       (conj [:h3 title])
      true        (into body))))

(defn pipeline-step
  "One step in the pipeline. opts:
     :title    — bold heading
     :subtitle — secondary line
     :note     — small badge (e.g. 'deterministic', 'micro / LLM')
     :note-class — :macro | :autonomous (color override)
     :tip      — tooltip body shown on hover (data-tip attr)"
  [{:keys [title subtitle note note-class tip]}]
  [:div.pipeline-step {:data-tip tip}
   [:strong title]
   [:span subtitle]
   [:span {:class (cond-> "step-note" note-class (str " " (name note-class)))} note]])

;; --- Page assembly ---

(defn html-page
  "Render a full HTML page. opts: {:title :description}. body is Hiccup."
  [opts & body]
  (str
   "<!DOCTYPE html>\n"
   (h/html
    [:html {:lang "en"}
     (head opts)
     [:body
      (nav)
      (beta-banner)
      body
      (footer)
      [:script (h/raw mobile-nav-script)]]])))
