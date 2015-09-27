(ns re.core
  (:require [clojure.string :as str]))

(declare re)

(defn group
  ([expressions] (group expressions true))
  ([expressions capturing?] (str "("
                                 (when-not capturing? "?:")
                                 (re expressions)
                                 ")")))

(defn zero-or-one [expressions]
  (str (if (> (count expressions) 1)
         (group expressions false)
         (re expressions))
       "?"))

(defn- or-more [ch expressions non-greedy?]
  (str (if (> (count expressions) 1)
         (group expressions false)
         (re expressions))
       (str ch (when non-greedy?
                  "?"))))

(defn one-or-more
  ([expressions] (one-or-more expressions false))
  ([expressions non-greedy?] (or-more \+ expressions non-greedy?)))

(defn zero-or-more
  ([expressions] (zero-or-more expressions false))
  ([expressions non-greedy?] (or-more \* expressions non-greedy?)))

(defn re-or
  ([expressions] (re-or expressions false))
  ([expressions capturing?] (group [(str/join "|" (map #(re [%]) expressions))] capturing?)))

(defn literal
  "Returns a quoted version of expression that can be used as a
  literal in a regexp."
  [expression]
  (java.util.regex.Pattern/quote expression))

(defn re [expressions]
  (loop [expressions expressions
         current ""]
    (let [command (first expressions)
          gen (str current (cond
                             (string? command) command
                             (= command :digit) "\\d"
                             (= command :non-digit) "\\D"
                             (= command :word) "\\w"
                             (= command :non-word) "\\W"
                             (= command :space) "\\s"
                             (= command :non-space) "\\S"))]
      (if (> (count (rest expressions)) 0)
        (recur (rest expressions) gen)
        gen))))
