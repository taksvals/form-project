(ns form.components.reusable.single-choice.core)

(defn single_choice [{:keys [id question values]}]
  [:div
   [:h3 question]
   (for [value values]
     [:div {:key value}
      [:input
       {:type "checkbox"
        :id (str id value)
        :name id
        :value value}]
      [:label
       {:for (str id value)}
       value]])])