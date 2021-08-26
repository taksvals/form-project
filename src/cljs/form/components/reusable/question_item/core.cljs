(ns form.components.reusable.question-item.core
  (:require [form.components.reusable.date.core :refer [date]]
            [form.components.reusable.free-text.core :refer [free_text]]
            [form.components.reusable.single-choice.core :refer [single_choice]]
            [form.components.reusable.multiple-choice.core :refer [multiple_choice]]
            [form.components.reusable.dropdown-list.core :refer [dropdown_list]]
            [form.components.reusable.linear-scale.core :refer [linear_scale]]))

(defn type-check [type]
  (condp = type
    "free-text"            free_text
    "date"                 date))

(defn type-with-value-check [type]
  (condp = type
    "single-choice"        single_choice
    "multiple-choice"      multiple_choice
    "dropdown-list"        dropdown_list
    "linear-scale"         linear_scale))

(defn question_item [{:keys [id type question values]}]
  (if values
     [(type-with-value-check type)
      {:id id
       :question question
       :values values}]
     [(type-check type)
      {:id id
       :question question}]))