(ns re.core
  (:require [clojure.string :as str]))

(declare re)

(defn group
  ([expressions] (group expressions true))
  ([expressions capturing?] (str "("
                                 (when-not capturing? "?:")
                                 (re expressions)
                                 ")")))

(defn- or-more [ch expressions non-greedy?]
  (str (if (> (count expressions) 1)
         (group expressions false)
         (re expressions))
       (str ch (when non-greedy?
                  "?"))))

(defn zero-or-one [expressions]
  (or-more \? expressions false))

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

(def ch-aliases (into {} (for [[expanded aliases] {"\\d" [:d :digit]
                                                   "\\D" [:D :non-digit]
                                                   "\\s" [:s :space]
                                                   "\\S" [:S :non-space]
                                                   "\\w" [:w :word]
                                                   "\\W" [:W :non-word]}
                               a aliases]
                           [a expanded])))

(defn re [expressions]
  (loop [expressions expressions
         current ""]
    (let [command (first expressions)
          gen (str current (cond
                             (string? command) command
                             (contains? ch-aliases command) (get ch-aliases command)))]
      (if (> (count (rest expressions)) 0)
        (recur (rest expressions) gen)
        gen))))
