(ns form.components.reusable.multiple-choice.core)

(defn multiple_choice [{:keys [id question values]}]
  [:div.question_item
   [:h3.question question]
   [:span "multiple answers"]
   [:div.checkbox-block
    (for [value values]
     [:div {:key value}
      [:input.checkbox-effect.checkbox-effect-5
       {:type "checkbox"
        :id (str id value)
        :name id
        :value value}]
      [:label
       {:for (str id value)}
       value]])]])