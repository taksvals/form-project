(ns form.components.pages.home.core
  (:require [form.components.reusable.question-item.core :refer [question_item]]
            [re-frame.core :as re-frame]
            [form.subs :as subs]
            [form.events :as events]
            [clojure.string :as string]
            [goog.array :as garray]
            
            [reagent.core :as r]))

(def error (r/atom {:text "" :style "hidden"}))

(def get-by-id ["free-text" "date" "dropdown-list"])

(def get-by-name ["single-choice" "multiple-choice"])

(defn error-block [text style]
  [:div {:class (str "error " style)}
   [:p (str "Questions: " text " were reqiured!")]])

(defn get-answers [questions]
  {:answers
   (into []
         (for [question questions
               :let [id (:id question)]]
           {:answer
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
            :id       (:id question)}))})

(defn answer-is-required? [out question]
  (if (true? (:required question))
    (assoc out :errors
           (conj (:errors out)
                 (:question question)))
    out))

(defn check-required [answers questions]
  (loop [all-questions questions
         all-answers (:answers answers)
         out         {:errors []}]
    (let [answer        (first all-answers)
          rest-answers  (rest all-answers)
          question      (first all-questions)
          rest-questions (rest all-questions)]
      (if answer
        (if (nil? (:answer answer))
          (recur rest-questions rest-answers 
                 (answer-is-required? out question))
          (recur rest-questions rest-answers
                 out))
        out))))

(defn clean-answers [questions]
  (doseq [question questions]
        (if (some #{(:type question)} get-by-id)
          (set! (.-value (.getElementById js/document (:id question))) "")
          (run! #(set! (.-checked %) false)
                (garray/toArray (.getElementsByTagName js/document "input"))))))
  
(defn on-click [questions]
  (let [answers (get-answers questions)
        errors  (:errors (check-required answers questions))]
    (reset! error {:text "" :style "hidden"})
    (if (> (count errors) 0)
      (reset! error {:text (string/join ", " errors) :style "danger"})
      (do
        (re-frame/dispatch-sync [::events/update-results (clj->js answers)])
        (clean-answers questions)))))

(defn home-page []
  (fn []
    (let [questions @(re-frame/subscribe [::subs/data])]
      [:div.container
       [:div.form
        [:div
         [:h1.title.center (:title questions)]
         [:p.light-text.center.small "Questions with * are required."]]
        [:form {:id "Form"}
         [error-block (:text @error) (:style @error)]
         (for [question (:questions questions)]
           [question_item
            {:key (:id question)
             :id (:id question)
             :type (:type question)
             :question (:question question)
             :values (:values question)
             :required (:required question)}])]
        [:div.wrap
         [:button.submit
          {:on-click #(on-click (:questions questions))}
          "Submit"]]]])))