(ns form.components.reusable.single-choice.core)

(defn single_choice [{:keys [id question values]}]
  [:div.question_item
   [:h3.question question]
   [:span "single answer"]
   [:div.checkbox-block
    (for [value values]
     [:div {:key value}
      [:input.checkbox-effect.checkbox-effect-5
       {:type "radio"
        :id (str id value)
        :name id
        :value value}]
      [:label
       {:for (str id value)}
       value]])]])