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

(defn one-or-more [expressions]
  (str (if (> (count expressions) 1)
         (group expressions false)
         (re expressions))
       "+"))

(defn re [expressions]
  (loop [expressions expressions
         current ""]
    (let [command (first expressions)
          gen (str current (cond
                             (string? command) command
                             (vector? command) (group [(str/join "|" (map #(re [%]) command))] false)
                             (= command :digit) "\\d"
                             (= command :non-digit) "\\D"
                             (= command :word) "\\w"
                             (= command :non-word) "\\W"
                             (= command :space) "\\s"
                             (= command :non-space) "\\S"))]
      (if (> (count (rest expressions)) 0)
        (recur (rest expressions) gen)
        gen))))
