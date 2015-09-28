(ns re.gen-docs
  (:require [clojure.string :as str]
            [re.core :refer [simple-aliases]]))

(defn gen-aliases-markdown []
  (doall
   (for [[out aliases]  simple-aliases]
     (println (str "* `" out "` (" (str/join ", " (map #(str "`" % "`") aliases)) ")")))))
