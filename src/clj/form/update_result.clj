(ns form.update-result
  (:require [cheshire.core :as ch]))

(defn vec->map [vec value]
  (into {} (for [k vec]
    (assoc {} (keyword k) value))))

(defn update-results-without-values [k result]
  (assoc result :values
         (assoc (:values result)
                k
                (if (k (:values result))
                  (inc (k (:values result)))
                  1))))


(defn update-results-with-values [answer result]
  (if (= (:type result) "multiple-choice")
    (assoc result :values
           (loop [answers (:answer answer)
                  out     (:values result)]
             (let [head (first answers)
                   tail (rest answers)]
                 (if head
                   (when (keyword head)
                     (recur tail 
                            (assoc out
                                   (keyword head)
                                   (inc ((keyword head) (:values result))))))
                   out))))
    (when (keyword (:answer answer))
      (assoc result :values
             (assoc (:values result)
                    (keyword (:answer answer))
                    (inc ((keyword (:answer answer)) (:values result))))))))

(def without-values ["free-text" "date"])

(defn answer-is-required? [out result]
  (if (:error out)
    (assoc out :error (conj
                       (:error out)
                       (str "Question: "
                            (:question result)
                            " was required!")))
    (assoc out :error (vector
                       (str "Question: "
                            (:question result)
                            " was required!")))))

(defn generate-result-template [questions]
  (let [questions questions
        out     {}]
    (assoc 
     (assoc out :title (:title questions)) 
     :results 
     (into [] (for [question (:questions questions)]
       (assoc 
        (assoc 
         (assoc {} :question (:question question))
         :type (:type question))
        :values (vec->map (:values question) 0)))))))

(def results (atom (generate-result-template (ch/parse-string (slurp "resources/form.json") true))))

(defn update-results [answers]
  (reset! results (loop [out         @results
                              answers     (:answers answers)
                              all-results (:results @results)
                              indexes     (range 0 (count answers))]
                         (let [answer        (first answers)
                               rest-answers  (rest answers)
                               result        (first all-results)
                               rest-results  (rest all-results)
                               current-index (first indexes)
                               rest-indexes (rest indexes)]
                           (if result
                             (if answer
                               (if (= (:question answer) (:question result))
                                 (recur (assoc out :results
                                               (assoc (:results out)
                                                      current-index
                                                      (if (some #{(:type result)} without-values)
                                                        (update-results-without-values (keyword (:answer answer)) result)
                                                        (update-results-with-values answer result))))
                                        rest-answers
                                        rest-results
                                        rest-indexes)
                                 (when (:required result)
                                   (recur (answer-is-required? out result)
                                          rest-answers
                                          rest-results
                                          rest-indexes)))
                               (recur (answer-is-required? out result)
                                      rest-answers
                                      rest-results
                                      rest-indexes))
                             out)))))