(ns re.core-test
  (:require [clojure.test :refer :all]
            [re.core :refer :all]))

(deftest re-test
  (testing "basics"
    (are [s r] (= s (apply re [r]))
      "\\d" [:digit]
      "\\D" [:non-digit]
      "\\w" [:word]
      "\\W" [:non-word]
      "\\s" [:space]
      "\\S" [:non-space]
      "\\S\\w" [:non-space :word]))

  (testing "string handling"
    (are [s r] (= s (re [r]))
      "(\\w\\s)" (group [:word :space])))

  (testing "or-ing"
    (is (= "(?:one|two)" (re-or ["one" "two"]))))

  (testing "groupings"
    (is (= "(\\w\\s)"    (group [:word :space])))
    (is (= "\\w+"        (one-or-more [:word])))
    (is (= "\\w?"        (zero-or-one [:word])))
    (is (= "(?:\\w\\s)?" (zero-or-one [:word :space])))
    (is (= "(?:\\w\\s)+" (one-or-more [:word :space])))))
