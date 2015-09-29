(ns gen-docs
  (:require [clojure.string :as str]
            [two-probs.core :refer :all]))

(defn gen-aliases-markdown []
  (doall
   (for [[out aliases]  simple-aliases]
     (println (str "* `" out "` (" (str/join ", " (map #(str "`" % "`") aliases)) ")")))))

(defn gen-funcs-markdown []
  (doseq [f [#'group
             #'cap
             #'non-cap
             #'zero-or-more
             #'zero-or-one
             #'one-or-more
             #'re-or
             #'literal
             #'times]]
    (println (str "* `"(:name (meta f)) "` - " (:doc (meta f))))))

(gen-funcs-markdown)
