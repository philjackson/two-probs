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
    (is (= "(\\w\\s)"     (group [:word :space])))
    (is (= "\\w+"         (one-or-more [:word])))
    (is (= "\\w+?"        (one-or-more [:word] true)))
    (is (= "\\w?"         (zero-or-one [:word])))
    (is (= "(?:\\w\\s)?"  (zero-or-one [:word :space])))
    (is (= "(?:\\w\\s)*"  (zero-or-more [:word :space])))
    (is (= "(?:\\w\\s)*?" (zero-or-more [:word :space] true)))
    (is (= "(?:\\w\\s)+"  (one-or-more [:word :space]))))

  (testing "escaping"
    (is (= "\\Qhello*world\\E" (literal "hello*world"))))

  (testing "aliases"
    (are [a1 a2] (= (re [a1]) (re [a2]))
      :d :digit
      :s :space))

  (testing "char class"
    (is (= "[abc\\wa]" (chrs ["a" "b" "c" :w \a])))))
