(ns noumenon-site.render
  "Page chrome: head, nav, footer wrapped around per-page Hiccup body."
  (:require [hiccup2.core :as h]
            [noumenon-site.styles :as styles]))

(def site-title "Noumenon")
(def site-description
  "Compile your repository into a Datomic knowledge graph. AI agents query structured facts instead of scanning raw source.")
(def site-url "https://noumenon.leifericf.com")

(def nav-items
  [{:href "https://github.com/leifericf/noumenon" :label "GitHub"}])

(defn- head [{:keys [title description]}]
  (let [page-title (if title (str title " — " site-title) site-title)
        desc       (or description site-description)]
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:title page-title]
     [:meta {:name "description" :content desc}]
     [:meta {:property "og:title" :content page-title}]
     [:meta {:property "og:description" :content desc}]
     [:meta {:property "og:url" :content site-url}]
     [:meta {:property "og:type" :content "website"}]
     [:meta {:name "twitter:card" :content "summary"}]
     [:link {:rel "icon" :type "image/svg+xml" :href "/favicon.svg"}]
     [:style (h/raw (styles/render))]]))

(defn- header []
  [:header.site-header
   [:nav.site-nav
    [:a.site-nav-brand {:href "/"} site-title]
    [:ul.site-nav-links
     (for [{:keys [href label]} nav-items]
       [:li [:a {:href href} label]])]]])

(defn- footer []
  [:footer.site-footer
   [:div.container
    [:p
     "Source: "
     [:a {:href "https://github.com/leifericf/noumenon"} "noumenon"] ", "
     [:a {:href "https://github.com/leifericf/noumenon-app"} "noumenon-app"] ", "
     [:a {:href "https://github.com/leifericf/noumenon-site"} "noumenon-site"]
     ". MIT license."]]])

(defn html-page
  "Render a full HTML page. opts: {:title :description}. body is Hiccup."
  [opts & body]
  (str
   "<!DOCTYPE html>\n"
   (h/html
    [:html {:lang "en"}
     (head opts)
     [:body
      (header)
      [:main.container body]
      (footer)]])))
