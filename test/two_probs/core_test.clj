(ns two-probs.core-test
  (:require [clojure.test :refer :all]
            [two-probs.core :refer :all]))

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

  (testing "char handling"
    (are [s r] (= s (re [r]))
      "sd"     (re [\s \d])
      "\\."    (re [\.])
      "\\{\\}" (re [\{ \}])))

  (testing "or-ing"
    (is (= "(?:one|two)" (re-or ["one" "two"]))))

  (testing "groupings"
    (is (= "(\\w+)"       (group [(one-or-more [:word])])))
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
    (is (= "[abc\\wa]" (chrs ["a" "b" "c" :w \a]))))

  (testing "beg/end"
    (is (= "^startend$" (re [:beg "startend" :end]))))

  (testing "any"
    (is (= "." (re [:.]))))

  (testing "times"
    (is (= "s{3,}" (times [\s] 3)))
    (is (= "s{3,10}" (times [\s] 3 10)))))

(deftest re-on-regexp-test
  (testing "markdown style heading"
    (let [heading (re-pattern
                   (re [:beg
                        (group (times \# 1 6))
                        (one-or-more :space)
                        (cap (zero-or-more [:any]))
                        :end]))]
      (is (= "hello world" (nth (re-matches heading "### hello world") 2)))))

  (testing "simple email regexp"
    (let [email (re-pattern
                 (re [:beg
                      (one-or-more :word true)
                      \@
                      (cap (one-or-more [:word]))
                      \.
                      (one-or-more :word)
                      :end]))]
      (is (= "example" (second (re-matches email "bob@example.com")))))))
