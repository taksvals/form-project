(ns form.components.reusable.date.core)

(defn date [{:keys [id question]}]
  [:div.question_item
   [:h3.question question]
   [:input.date {:type "date" :name "date" :id id}]])