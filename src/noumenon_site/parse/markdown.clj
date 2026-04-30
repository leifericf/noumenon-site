(ns noumenon-site.parse.markdown
  "Block-level markdown → Hiccup parser. Handles ##/### headings,
   bullet lists with continuation, fenced code blocks (``` with
   optional language), pipe tables, and paragraphs. Inline formatting
   delegates to noumenon-site.format/inline."
  (:require [clojure.string :as str]
            [hiccup.util :as hu]
            [noumenon-site.format :as fmt]))

(defn- slug
  [text]
  (-> text
      str/lower-case
      (str/replace #"[^\w\d]+" "-")
      (str/replace #"^-|-$" "")))

(defn- inline-hiccup
  [s]
  (hu/raw-string (fmt/inline s)))

(defn- take-bullet-block
  "Consume a contiguous bullet block, returning [items remaining-lines].
   Continuation lines (indented) get appended to the previous item."
  [lines]
  (loop [ls lines
         items []]
    (let [l (first ls)]
      (cond
        (nil? l) [items ls]
        (str/starts-with? l "- ")
        (recur (rest ls) (conj items (subs l 2)))
        (and (seq items) (str/starts-with? l "  "))
        (recur (rest ls)
               (update items (dec (count items)) str " " (str/trim l)))
        :else [items ls]))))

(defn- take-paragraph
  "Consume consecutive non-blank, non-structural lines into one paragraph
   so markdown soft-wrap renders as a single <p>."
  [lines]
  (loop [ls lines
         acc []]
    (let [l (first ls)]
      (cond
        (nil? l) [acc ls]
        (or (str/blank? l)
            (str/starts-with? l "#")
            (str/starts-with? l "- ")
            (str/starts-with? l "```")
            (str/starts-with? l "|"))
        [acc ls]
        :else (recur (rest ls) (conj acc l))))))

(defn- take-fenced-code
  "Consume a fenced code block. fence-line is the opening ``` line,
   possibly with a language (```bash). Returns [hiccup-block remaining]."
  [fence-line lines]
  (let [lang (str/trim (subs fence-line 3))
        [body remaining]
        (loop [ls lines
               acc []]
          (let [l (first ls)]
            (cond
              (nil? l) [acc ls]
              (str/starts-with? l "```") [acc (rest ls)]
              :else (recur (rest ls) (conj acc l)))))
        code (fmt/escape-html (str/join "\n" body))
        attrs (cond-> {} (seq lang) (assoc :data-lang lang))]
    [[:pre [:code attrs (hu/raw-string code)]] remaining]))

(defn- table-row
  [line]
  (->> (str/split line #"\|")
       (drop 1)
       (drop-last)
       (map str/trim)))

(defn- separator-row?
  [line]
  (boolean
   (and (str/starts-with? line "|")
        (re-matches #"\|[\s\-:]+(\|[\s\-:]+)*\|?" line))))

(defn- take-table
  "Consume a pipe-table block. The first line is the header, the second
   is the separator (---), subsequent are rows. Returns [hiccup remaining]."
  [first-line lines]
  (let [header (table-row first-line)
        sep    (first lines)]
    (if (and sep (separator-row? sep))
      (let [[rows remaining]
            (loop [ls (rest lines)
                   acc []]
              (let [l (first ls)]
                (if (and l (str/starts-with? l "|"))
                  (recur (rest ls) (conj acc (table-row l)))
                  [acc ls])))
            thead [:thead
                   [:tr (for [h header] [:th (inline-hiccup h)])]]
            tbody (into [:tbody]
                        (for [row rows]
                          [:tr (for [c row] [:td (inline-hiccup c)])]))]
        [[:table.md-table thead tbody] remaining])
      ;; Not a real table — fall back to paragraph
      [[:p (inline-hiccup first-line)] lines])))

(defn parse
  "Convert markdown text into a vector of Hiccup blocks. Top-level
   `# Heading` is dropped (the calling page provides its own h1)."
  [md]
  (let [lines (str/split-lines md)
        lines (drop-while #(not (or (str/starts-with? % "## ")
                                    (str/starts-with? % "```")
                                    (str/starts-with? % "|")
                                    (str/starts-with? % "- ")))
                          lines)]
    (loop [lines lines
           result []]
      (if (empty? lines)
        result
        (let [line (first lines)
              rest-lines (rest lines)]
          (cond
            (str/starts-with? line "# ")
            (recur rest-lines result)

            (str/starts-with? line "## ")
            (let [text (subs line 3)]
              (recur rest-lines
                     (conj result [:h2 {:id (slug text)} text])))

            (str/starts-with? line "### ")
            (let [text (subs line 4)]
              (recur rest-lines
                     (conj result [:h3 {:id (slug text)} text])))

            (str/starts-with? line "```")
            (let [[block remaining] (take-fenced-code line rest-lines)]
              (recur remaining (conj result block)))

            (str/starts-with? line "- ")
            (let [[items remaining] (take-bullet-block (cons line rest-lines))]
              (recur remaining
                     (conj result
                           (into [:ul]
                                 (for [item items]
                                   [:li (inline-hiccup item)])))))

            (str/starts-with? line "|")
            (let [[block remaining] (take-table line rest-lines)]
              (recur remaining (conj result block)))

            (str/blank? line)
            (recur rest-lines result)

            :else
            (let [[para-lines remaining] (take-paragraph (cons line rest-lines))]
              (recur remaining
                     (conj result
                           [:p (inline-hiccup (str/join " " para-lines))])))))))))
