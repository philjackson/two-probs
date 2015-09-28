(ns re.core
  (:require [clojure.string :as str]))

(declare re)

(defn group
  ([expressions] (group expressions true))
  ([expressions capturing?] (str "("
                                 (when-not capturing? "?:")
                                 (re expressions)
                                 ")")))

(defn- or-more [ch expressions reluctantly?]
  (str (if (> (count expressions) 1)
         (group expressions false)
         (re expressions))
       (str ch (when reluctantly?
                  "?"))))

(defn zero-or-one [expressions]
  (or-more \? expressions false))

(defn one-or-more
  ([expressions] (one-or-more expressions false))
  ([expressions reluctantly?] (or-more \+ expressions reluctantly?)))

(defn zero-or-more
  ([expressions] (zero-or-more expressions false))
  ([expressions reluctantly?] (or-more \* expressions reluctantly?)))

(defn re-or
  ([expressions] (re-or expressions false))
  ([expressions capturing?] (group [(str/join "|" (map #(re [%]) expressions))] capturing?)))

(defn literal
  "Returns a quoted version of expression that can be used as a
  literal in a regexp."
  [expression]
  (java.util.regex.Pattern/quote expression))

(defn chrs [cs]
  (str "[" (re cs) "]"))

;; we transform this into a hash that looks like this:
;; {:d "\\d" :digit "\\d" ... }
(def ch-aliases (into {} (for [[expanded aliases] {"^" [:start :beg :beginning]
                                                   "$" [:$ :end]

                                                   "\\d" [:d :digit]
                                                   "\\D" [:D :non-digit]
                                                   "\\s" [:s :space]
                                                   "\\S" [:S :non-space]
                                                   "\\w" [:w :word]
                                                   "\\W" [:W :non-word]
                                                   "."   [:. :any]

                                                   "\\b" [:b :boundary :bndry]
                                                   "\\B" [:B :non-boundary :non-bndry]
                                                   "\\A" [:A :beginning-input :beg-input :beg-in]
                                                   "\\z" [:z :end-input :end-in]
                                                   "\\G" [:G :end-match]
                                                   "\\Z" [:Z :end-term]}
                               a aliases]
                           [a expanded])))

(def need-escaping #{\[\]\*\+\.\^\$\\\{\}})

(defn perhaps-escape [ch]
  (if (some need-escaping [ch])
    (str "\\" ch)
    ch))

(defn times
  ([expressions mn] (times expressions mn nil))
  ([expressions mn mx] (str (re expressions)
                            (if mx
                              (str "{" mn "," mx "}")
                              (str "{" mn ",}")))))

(defn re
  "Loop over `expressions' and gradually build a string suitable for a
  regular expression."
  [expressions]
  (loop [expressions expressions
         current ""]
    (let [command (first expressions)
          gen (str current (cond
                             (string? command) command
                             (char? command) (perhaps-escape command)
                             (contains? ch-aliases command) (get ch-aliases command)))]
      (if (> (count (rest expressions)) 0)
        (recur (rest expressions) gen)
        gen))))
