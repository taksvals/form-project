(ns form.components.reusable.free-text.core)

(defn free_text [{:keys [id question]}]
  [:div.question_item
   [:h3.question question]
   [:input.form__field {:type "text" :name "first-name" :id id}]])