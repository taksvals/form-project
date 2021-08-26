(ns form.components.reusable.dropdown-list.core)

(defn dropdown_list [{:keys [id question values]}]
  [:div.question_item
   [:h3.question question]
   [:select.drop_down
    {
     :id id
     :name "dropdown-values"}
    [:option.drop_down_option {:key (str "default" id) :id (str "default" id)}]
    (for [value values]
      [:option.drop_down_option {:key value :id (str id value)}
       value])
    ]])