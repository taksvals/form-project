(ns form.update-result
  (:require [cheshire.core :as ch]))

(def without-values ["free-text" "date"])

(defn vec->map [vec value]
  (into {} (for [k vec]
    {(keyword k) value})))

(defn generate-result-template [questions]
  (let [questions questions]
    {:title (:title questions)
     :results 
     (into [](for [question (:questions questions)]
               {:id       (:id question)
                :type     (:type question)
                :question (:question question)
                :required (:required question)
                :values   (vec->map (:values question) 0)}))}))

(def results (atom (generate-result-template (ch/parse-string (slurp "resources/form.json") true))))

(defn update-results-without-values [k result]
  (if (k (:values result))
    (update-in result [:values k] inc)
    (assoc-in result [:values k] 1)))

(defn update-results-with-values [answer result]
  (if (= (:type result) "multiple-choice")
    (loop [answs answer
           out   result]
      (let [answ       (first answs)
            rest-answs (rest answs)]
        (if answ
          (recur rest-answs
                 (update-in out [:values (keyword answ)] inc))
          out)))
    (update-in result [:values (keyword answer)] inc)))

(defn update-results [answers]
  (swap! results
         assoc :results
         (map
          (fn [answer result]
            (if (:answer answer)
              (let [answ (:answer answer)]
                (if (some #{(:type result)} without-values)
                  (update-results-without-values (keyword answ) result)
                  (update-results-with-values answ result)))
              result))
          (sort-by :id (:answers answers))
          (sort-by :id (:results @results)))))

(defn count-percentage[values]
  (loop [values values
         sum    (reduce + (vals values))
         out    {}]
    (let [value       (first values)
          rest-values (rest values)
          sum         (if (= sum 0) 
                        1
                        sum)]
      (if value
        (recur rest-values sum
               (assoc out (key value)
                      (* (/ (val value) sum) 100)))
        out))))

(defn update-results-statistic [results]
  (assoc-in results 
            [:results]
            (map
             (fn [result]
               (update-in
                (if (some #{(:type result)} without-values)
                  (update result
                          :values
                          #(take 5 (sort-by val > %)))
                  result)
                [:values] count-percentage))
             (:results results))))