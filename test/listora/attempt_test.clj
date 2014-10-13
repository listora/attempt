(ns listora.attempt-test
  (:require [clojure.test :refer :all]
            [listora.attempt :refer :all]))

(deftest test-attempt
  (testing "asynchronous"
    (let [t (System/currentTimeMillis)]
      (attempt #(Thread/sleep 1000))
      (is (< (- (System/currentTimeMillis) t) 100))))

  (testing "returned promise"
    (is (= @(attempt #(+ 1 1)) 2)))

  (testing "default fallback"
    (is (nil? @(attempt #(/ 1 0)))))

  (testing "fallback option"
    (is (= @(attempt #(/ 1 0) {:fallback :error}) :error)))

  (testing "default no retries"
    (let [tries (atom 0)]
      @(attempt #(do (swap! tries inc) (/ 1 0)))
      (is (= @tries 1))))

  (testing "retries option"
    (let [tries (atom 0)]
      @(attempt #(do (swap! tries inc) (/ 1 0)) {:retries [0 0 0]})
      (is (= @tries 4))))

  (testing "retries timing"
    (let [tries (atom 0)]
      (attempt #(do (swap! tries inc) (/ 1 0)) {:retries [100 100]})
      (Thread/sleep 80)
      (is (= @tries 1))
      (Thread/sleep 80)
      (is (= @tries 2))
      (Thread/sleep 80)
      (is (= @tries 3)))))
