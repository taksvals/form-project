(ns form.components.reusable.free-text.core)

(defn free_text [{:keys [id question]}]
  [:div
   [:h3 question]
   [:input {:type "text" :name "first-name" :id id}]])