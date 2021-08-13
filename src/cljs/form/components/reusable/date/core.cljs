(ns form.components.reusable.date.core)

(defn date [{:keys [id question]}]
  [:div
   [:h3 question]
   [:input {:type "date" :name "date" :id id}]])