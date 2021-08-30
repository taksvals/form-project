(ns form.components.reusable.free-text.core)

(defn free_text [{:keys [id question required]}]
  [:div.question-item
   [:div.question-heading
    [:h3 question]
   (when required
     [:h3.required " *"])]
   [:input.form-field {:type "text" :name "first-name" :id id}]])