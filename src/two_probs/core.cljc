(ns two-probs.core
  (:require [clojure.string :as str]))

(declare re)

(defn- vectorise-maybe [expressions]
  (if (coll? expressions) expressions [expressions]))

(defn group
  "Group `expressions`, truthy final arity determines capturing
  status."
  ([expressions] (group expressions true))
  ([expressions capturing?] (str "("
                                 (when-not capturing? "?:")
                                 (re expressions)
                                 ")")))

(defn cap
  "Capturing group."
  [expressions]
  (group expressions true))

(defn non-cap
  "Non-capturing group."
  [expressions]
  (group expressions false))

(defn- or-more [ch expressions reluctantly?]
  (let [expressions (vectorise-maybe expressions)]
    (str (if (> (count expressions) 1)
           (non-cap expressions)
           (re expressions))
         (str ch (when reluctantly?
                   "?")))))

(defn zero-or-one [expressions]
  "Match zero or one instances of `expressions`."
  (or-more \? expressions false))

(defn one-or-more
  "Match one or more instances of `expressions`."
  ([expressions] (one-or-more expressions false))
  ([expressions reluctantly?] (or-more \+ expressions reluctantly?)))

(defn zero-or-more
  "Match zero or more instances of `expressions`."
  ([expressions] (zero-or-more expressions false))
  ([expressions reluctantly?] (or-more \* expressions reluctantly?)))

(defn re-or
  "Returns `expressions` logically or'd together."
  ([expressions] (re-or expressions false))
  ([expressions capturing?] (group [(str/join "|" (map #(re [%]) expressions))] capturing?)))

(defn literal
  "Returns a quoted version of expression that can be used as a
  literal in a regexp."
  [expression]
  #?(:clj  (java.util.regex.Pattern/quote expression)
     :cljs (let [special (set ".?*+^$[]\\(){}|")
                 escfn #(if (special %) (str \\ %) %)]
             (apply str (map escfn expression)))))

(defn chrs
  "Builds a character class out of `cs`."
  [cs]
  (str "[" (re cs) "]"))

(def simple-aliases {"^" [:start :beg :beginning]
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
                     "\\Z" [:Z :end-term]})

;; we transform this into a hash that looks like this:
;; {:d "\\d" :digit "\\d" ... }
(def denormalised-aliases (into {} (for [[expanded aliases] simple-aliases
                                         a aliases]
                                     [a expanded])))

(def need-escaping #{\[\]\*\+\.\^\$\\\{\}})

(defn perhaps-escape
  "Escape a `ch' if it's a special re char."
  [ch]
  (if (some need-escaping [ch])
    (str "\\" ch)
    ch))

(defn times
  "Match `expression` a minimum of `mn` times and a maximum of `mx`
  times."
  ([expressions mn] (times expressions mn nil))
  ([expressions mn mx] (str (re expressions)
                            (if mx
                              (str "{" mn "," mx "}")
                              (str "{" mn ",}")))))

(defn re
  "Loop over `expressions` and gradually build a string suitable for a
  regular expression."
  [expressions]
  (loop [expressions (vectorise-maybe expressions)
         current ""]
    (let [exp (first expressions)
          gen (str current (cond
                             (string? exp) exp
                             (char? exp) (perhaps-escape exp)
                             (contains? denormalised-aliases exp) (get denormalised-aliases exp)))]
      (if (> (count (rest expressions)) 0)
        (recur (rest expressions) gen)
        gen))))
