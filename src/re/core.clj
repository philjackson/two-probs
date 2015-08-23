(ns re.core)

(declare re)

(defn group [expressions]
  (str "(" (apply re expressions) ")"))

(defn re [& expressions]
  (loop [expressions expressions
         current ""]
    (let [command (first expressions)]
      (let [gen (str current (cond
                               (vector? command) (case (first command)
                                                   :group (group (rest command)))
                               (= command :digit) "\\d"
                               (= command :non-digit) "\\D"
                               (= command :word) "\\w"
                               (= command :non-word) "\\W"
                               (= command :space) "\\s"
                               (= command :non-space) "\\S"))]
        (if (> (count (rest expressions)) 0)
          (recur (rest expressions) gen)
          gen)))))
