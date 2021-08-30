(ns form.components.reusable.question-item.core
  (:require [form.components.reusable.date.core :refer [date]]
            [form.components.reusable.free-text.core :refer [free_text]]
            [form.components.reusable.single-choice.core :refer [single_choice]]
            [form.components.reusable.multiple-choice.core :refer [multiple_choice]]
            [form.components.reusable.dropdown-list.core :refer [dropdown_list]]))

(defn type-without-values-check [type]
  (condp = type
    "free-text"            free_text
    "date"                 date))

(defn type-with-value-check [type]
  (condp = type
    "single-choice"        single_choice
    "multiple-choice"      multiple_choice
    "dropdown-list"        dropdown_list))

(defn question_item [{:keys [id type question values required]}]
  (if values
     [(type-with-value-check type)
      {:id id
       :question question
       :values values
       :required required}]
     [(type-without-values-check type)
      {:id id
       :question question
       :required required}]))