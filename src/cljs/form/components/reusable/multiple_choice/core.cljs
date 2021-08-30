(ns form.components.reusable.multiple-choice.core)

(defn multiple_choice [{:keys [id question values required]}]
  [:div.question-item
   [:div.question-heading
    [:h3.question question]
    (when required
      [:h3.required "*"])]
   [:p.light-text "multiple answers"]
   [:div.buttons-block
    (for [value values]
      [:div {:key value}
       [:input.button-effect.button-effect-5
        {:type "checkbox"
         :id (str id value)
         :name id
         :value value}]
       [:label
        {:for (str id value)}
        value]])]])