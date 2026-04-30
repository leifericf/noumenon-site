(ns noumenon-site.render
  "Page chrome and shared building blocks (head, nav, footer, terminal, cards)."
  (:require [hiccup2.core :as h]
            [noumenon-site.highlight :as highlight]
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
(def ga-measurement-id "G-LD8F7JFYGB")
(def ga-hostname "noumenon.leifericf.com")

(def nav-items
  [{:href "/get-started/" :label "Install"    :page :install}
   {:href "/concepts/"    :label "Concepts"   :page :concepts}
   {:href "/reference/"   :label "Reference"  :page :reference}
   {:href "/server/"      :label "Server"     :page :server}
   {:href "https://github.com/leifericf/noumenon"
    :label "GitHub"
    :external true}])

(def beta-notice
  "Experimental, early beta — data model and interfaces are unstable. Expect breaking changes between releases.")

;; --- Head ---

(defn- ga-script-tag []
  [:script {:async true
            :src (str "https://www.googletagmanager.com/gtag/js?id=" ga-measurement-id)}])

(defn- ga-config-script []
  [:script (h/raw
            (str "if(location.hostname==='" ga-hostname "'){"
                 "window.dataLayer=window.dataLayer||[];"
                 "function gtag(){dataLayer.push(arguments);}"
                 "gtag('js',new Date());"
                 "gtag('config','" ga-measurement-id "',{anonymize_ip:true});"
                 "}"))])

(defn- head [{:keys [title description]}]
  (let [page-title (if title
                     (str title " | " site-title)
                     site-tagline)
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
     [:style (h/raw (styles/render))]
     (ga-script-tag)
     (ga-config-script)]))

;; --- Nav ---

(defn- nav-toggle-icon []
  [:svg {:width 20 :height 20 :viewBox "0 0 20 20" :fill "currentColor"}
   [:rect {:y  3 :width 20 :height 2 :rx 1}]
   [:rect {:y  9 :width 20 :height 2 :rx 1}]
   [:rect {:y 15 :width 20 :height 2 :rx 1}]])

(defn- nav-link [active-page {:keys [href label page external]}]
  [:li
   [:a (cond-> {:href href}
         (and page (= page active-page)) (assoc :class "active")
         external (assoc :target "_blank" :rel "noopener"))
    label
    (when external " ↗")]])

(defn- nav [active-page]
  [:nav.nav
   [:div.container
    [:a.nav-brand {:href "/"} site-title]
    [:button.nav-toggle {:aria-label "Menu"} (nav-toggle-icon)]
    [:ul.nav-links
     (for [item nav-items]
       (nav-link active-page item))]]])

(defn- beta-banner []
  [:div.beta-banner.container
   [:p (h/raw beta-notice)]])

;; --- Footer ---

(def footer-sections
  [{:heading "Get started"
    :links [{:href "/get-started/" :label "Install"}
            {:href "/mcp/"         :label "MCP setup"}
            {:href "/server/"      :label "Self-host"}]}
   {:heading "Learn"
    :links [{:href "/concepts/"                     :label "Concepts"}
            {:href "/concepts/knowledge-graph/"     :label "Knowledge graph"}
            {:href "/concepts/pipeline/"            :label "Pipeline"}
            {:href "/concepts/introspect/"          :label "Introspect"}
            {:href "/concepts/benchmarks/"          :label "Benchmarks"}]}
   {:heading "Reference"
    :links [{:href "/reference/" :label "Overview"}
            {:href "/queries/"   :label "Queries"}
            {:href "/api/"       :label "HTTP API"}
            {:href "/mcp/"       :label "MCP tools"}]}
   {:heading "Project"
    :links [{:href "https://github.com/leifericf/noumenon"             :label "GitHub"}
            {:href "/changelog/"                                       :label "Changelog"}
            {:href "https://github.com/leifericf/noumenon/blob/main/LICENSE"
             :label "MIT License"}]}])

(defn- footer []
  [:footer.footer
   [:div.container
    [:div.footer-grid
     (for [{:keys [heading links]} footer-sections]
       [:div.footer-col
        [:h4 heading]
        [:ul
         (for [{:keys [href label]} links]
           [:li [:a {:href href} label]])]])]
    [:p.footer-credit "Leif Eric Fredheim"]]])

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

;; --- Building blocks (used by content namespaces) ---

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
  "Render a full HTML page. opts: {:title :description :active-page :show-banner?}.
   body is Hiccup. :show-banner? defaults true."
  [{:keys [active-page show-banner?] :or {show-banner? true} :as opts} & body]
  (str
   "<!DOCTYPE html>\n"
   (h/html
    [:html {:lang "en"}
     (head opts)
     [:body
      (nav active-page)
      (when show-banner? (beta-banner))
      [:main body]
      (footer)
      [:script (h/raw mobile-nav-script)]
      (when (seq highlight/highlight-js)
        [:script (h/raw highlight/highlight-js)])]])))
