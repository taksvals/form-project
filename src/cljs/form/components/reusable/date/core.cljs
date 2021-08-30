(ns form.components.reusable.date.core)

(defn date [{:keys [id question required]}]
  [:div.question-item
   [:div.question-heading
    [:h3.question question]
    (when required
     [:h3.required "*"])]
   [:input.date {:type "date" :name "date" :id id}]])