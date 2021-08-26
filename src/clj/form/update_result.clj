(ns form.update-result
  (:require [cheshire.core :as ch]))

(defn vec->map [vec value]
  (into {} (for [k vec]
    (assoc {} (keyword k) value))))

(def without-values ["free-text" "date"])

(defn update-results-without-values [k result]
  (if (k (:values result))
    (update-in result [:values k] inc)
    (assoc result :values 
           (assoc (:values result) k 1))))

(defn update-results-with-values [answer result]
  (if (= (:type result) "multiple-choice")
    (loop [answs answer
           out     result]
      (let [answ       (first answs)
            rest-answs (rest answs)]
        (if answ
          (recur rest-answs
                 (update-in out
                            [:values (keyword answ)]
                            inc))
          out)))
    (update-in result [:values (keyword answer)] inc)))

(defn answer-is-required? [out result]
  (if (:required result)
    (if (:errors out)
      (assoc out :errors
             (conj
              (:errors out)
              (str "Question: "
                   (:question result)
                   " was required!")))
      (assoc out :errors
             (vector
              (str "Question: "
                   (:question result)
                   " was required!"))))
    out))

(defn generate-result-template [questions]
  (let [questions questions]
    (hash-map
     :title (:title questions)
     :results 
     (into [](for [question (:questions questions)]
               (hash-map :id       (:id question)
                         :type     (:type question)
                         :question (:question question)
                         :required (:required question)
                         :values   (vec->map (:values question) 0)))))))

(def results (atom (generate-result-template (ch/parse-string (slurp "resources/form.json") true))))

(defn check-required [answers]
  (loop [all-results (:results @results)
         all-answers (:answers answers)
         out         @results]
    (let [answer       (first all-answers)
          rest-answers (rest all-answers)
          result       (first all-results)
          rest-results (rest all-results)]
      (if answer
        (if (nil? (:answer answer))
          (recur rest-answers rest-results
                 (answer-is-required? out result))
          (recur rest-answers rest-results
                 out))
        out))))

(defn update-results [answers]
  (assoc @results :results
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

(defn handle-answers[answers]
  (let [all-results (check-required answers)]
   (if (:errors all-results)
     (:errors all-results)
     (reset! results (update-results answers)))))

(comment
  (handle-answers {:answers
                   [{:id "1"
                     :question "What's your name?"
                     :answer "Tanya"}
                    {:id "2"
                     :question "Date of birth"
                     :answer nil}
                    {:id "3"
                     :question "What is your position1?"
                     :answer nil}
                    {:id "4"
                     :question "What is your position2?"
                     :answer nil}
                    {:id "5"
                     :question "What is your position3?"
                     :answer nil}]})

  (check-required {:answers
                   [{:id "1"
                     :question "What's your name?"
                     :answer nil}
                    {:id "2"
                     :question "Date of birth"
                     :answer "2021-07-29"}
                    {:id "3"
                     :question "What is your position1?"
                     :answer "Teacher1"}
                    {:id "4"
                     :question "What is your position2?"
                     :answer ["Teacher2" "Student2"]}
                    {:id "5"
                     :question "What is your position3?"
                     :answer "Teacher3"}]})

  (update-results {:answers
                   [{:id "1"
                     :question "What's your name?"
                     :answer "Tanya"}
                    {:id "2"
                     :question "Date of birth"
                     :answer "2021-07-29"}
                    {:id "3"
                     :question "What is your position1?"
                     :answer "Teacher1"}
                    {:id "4"
                     :question "What is your position2?"
                     :answer ["Teacher2" "Student2"]}
                    {:id "5"
                     :question "What is your position3?"
                     :answer "Teacher3"}]})

  (generate-result-template {:title "About Yourself"
                             :questions
                             [{:id "1", :type "free-text", :question "What's your name?", :required true}
                              {:id "2", :type "date", :question "Date of birth"}
                              {:id "3"
                               :type "dropdown-list"
                               :question "What is your position1?"
                               :values ["Teacher1" "Student1" "Administrator1" "Scientist1"]}
                              {:id "4"
                               :type "multiple-choice"
                               :question "What is your position2?"
                               :values ["Teacher2" "Student2" "Administrator2" "Scientist2"]}
                              {:id "5"
                               :type "single-choice"
                               :question "What is your position3?"
                               :values ["Teacher3" "Student3" "Administrator3" "Scientist3"]}]})
  )