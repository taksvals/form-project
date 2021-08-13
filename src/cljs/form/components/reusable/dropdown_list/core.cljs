(ns form.components.reusable.dropdown-list.core)

(defn dropdown_list [{:keys [id question values]}]
  [:div
   [:h3 question]
   [:select 
    {
     :id id
     :name "dropdown-values"}
    (for [value values]
      [:option {:key value :id (str id value)}
       value])
    ]])