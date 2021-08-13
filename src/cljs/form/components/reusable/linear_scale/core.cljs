(ns form.components.reusable.linear-scale.core)

(defn linear_scale [{:keys [question values]}]
  [:div
   [:h3 question]
   (for [value (range (first values) (inc (second values)))]
     [:div {:key value}
      [:input
       {:type "radio"
        :id value
        :name "value"
        :value value}]
      [:label
       {:for value}
       value]])])