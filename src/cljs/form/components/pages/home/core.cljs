(ns form.components.pages.home.core
  (:require [form.components.reusable.question-item.core :refer [question_item]]
            [re-frame.core :as re-frame]
            [form.subs :as subs]
            [form.events :as events]))

(def get-by-id ["free-text" "date" "dropdown-list"])

(def get-by-name ["single-choice" "multiple-choice"])

(defn on-click [questions]
  (js/console.log
   (into {}
         (for [question questions
               :let [id (:id question)]]
           (hash-map (keyword id)
                     (hash-map :answer
                               (if (some #{(:type question)} get-by-id)
                                 (.-value (.getElementById js/document id))
                                 (if (= (:type question) "multiple-choice")
                                   (.-value (.querySelector js/document  (str ".form input[name='" id "']:checked")))
                                   (into []
                                         (for [value (.querySelectorAll js/document  (str ".form input[name='" id "']:checked"))]
                                           (.-value value)))))))))))

(defn home-page []
  (re-frame/dispatch-sync [::events/load-questions])
  (fn []
    (let [questions (:questions @(re-frame/subscribe [::subs/data]))]
      (js/console.log questions)
     [:div.container
      [:div.form
       [:h1 "Form"]
       [:form
        (for [question questions]
          [question_item
           {:id (:id question)
            :type (:type question)
            :question (:question question)
            :values (:values question)}])]
       [:button 
        {:on-click #(on-click questions)}
        "Submit"]]])))