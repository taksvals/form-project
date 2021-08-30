(ns form.components.reusable.dropdown-list.core)

(defn dropdown_list [{:keys [id question values required]}]
  [:div.question-item
   [:div.question-heading
    [:h3.question question]
    (when required
     [:h3.required "*"])]
   [:select.drop-down
    {:id id
     :name "dropdown-values"}
    [:option.drop-down-option {:key (str "default" id) :id (str "default" id)}]
    (for [value values]
      [:option.drop-down-option {:key value :id (str id value)}
       value])
    ]])