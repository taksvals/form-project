(ns form.components.reusable.single-choice.core)

(defn single_choice [{:keys [id question values required]}]
  [:div.question-item
   [:div.question-heading
    [:h3.question question]
    (when required
      [:h3.required "*"])]
   [:p.light-text "single answer"]
   [:div.buttons-block
    (for [value values]
      [:div {:key value}
       [:input.button-effect.button-effect-5
        {:type "radio"
         :id (str id value)
         :name id
         :value value}]
       [:label
        {:for (str id value)}
        value]])]])