(ns form.components.pages.home.core
  (:require [form.components.reusable.question-item.core :refer [question_item]]
            [re-frame.core :as re-frame]
            [form.subs :as subs]
            [form.events :as events]))

(def get-by-id ["free-text" "date" "dropdown-list"])

(def get-by-name ["single-choice" "multiple-choice"])

;; (def get-by-id-2 ["free-text"])

(defn get-answers [questions]
  (hash-map :answers
            (into []
                  (for [question questions
                        :let [id (:id question)]]
                    (hash-map
                     :answer
                     (if (some #{(:type question)} get-by-id)
                       (when (seq (.-value (.getElementById js/document id)))
                         (.-value (.getElementById js/document id)))
                       (if (= (:type question) "single-choice")
                         (when (.querySelector js/document  (str ".form input[name='" id "']:checked"))
                           (.-value (.querySelector js/document  (str ".form input[name='" id "']:checked"))))
                         (when (seq (.querySelectorAll js/document  (str ".form input[name='" id "']:checked")))
                           (into []
                                 (for [value (.querySelectorAll js/document  (str ".form input[name='" id "']:checked"))]
                                   (.-value value))))))
                     :question (:question question)
                     :id       (:id question))))))

;; (defn clean-answers [questions]
;;   (for [question questions
;;         :let [id (:id question)]]
;;     (if (some #{(:type question)} get-by-id-2)
;;       
;;       )))

;; (defn answers-check [answers]
;;   (loop [answers (:answers answers)
;;          out     {:answers []}]
;;     (let [answer (first answers)
;;           rest-answers (rest answers)]
;;       (if answer
;;         (if (:answer answer)
;;           (recur rest-answers (assoc out :answers 
;;                                      (conj (:answers out) answer)))
;;           (recur rest-answers out))
;;         out))))

(defn on-click [questions]
  (let [answers (get-answers questions)]
    (re-frame/dispatch-sync [::events/update-results (clj->js answers)])
    ))

(defn home-page []
  (fn []
    (let [questions @(re-frame/subscribe [::subs/data])]
     [:div.container
      [:div.form
       [:h1.title (:title questions)]
       [:form {:id "Form"}
        (for [question (:questions questions)]
          [question_item
           {:key (:id question)
            :id (:id question)
            :type (:type question)
            :question (:question question)
            :values (:values question)}])]
       [:div.wrap
        [:button.submit
         {:on-click #(on-click (:questions questions))}
         "Submit"]]]])))