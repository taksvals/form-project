(ns form.components.pages.statistic.core
  (:require [reagent.core :as r]
            [bizcharts :refer [Chart Interval Tooltip]]
            [re-frame.core :as re-frame]
            [form.events :as events]
            [form.subs :as subs]))

(def chart (r/adapt-react-class Chart))
(def interval (r/adapt-react-class Interval))
(def tooltip (r/adapt-react-class Tooltip))

(defn data-parse [values]
  (into [] (for [[k v] values]
    (assoc (assoc {} :key k) :value v))))

(defn count-percentage[values]
  (loop [values values
         sum    (reduce + (vals values))
         out    {}]
    (let [value (first values)
          rest-values (rest values)]
      (if value
        (recur rest-values 
               sum
               (assoc
                out
                (key value)
                (* (/ (val value) sum) 100)))
        out))))

;; (comment
;; (count-percentage {:Teacher1 0
;;                    :Student1 1 
;;                    :Administrator1 2
;;                    :Scientist1 2}))
(defn statistic-page []
  (re-frame/dispatch-sync [::events/load-results])
  (fn [] (let [results (:results @(re-frame/subscribe [::subs/results]))] 
           (prn results)
           [:div
           [:span.main
            [:h1 "Statistic"]]
           (for [result results]
             (when (seq (:values result))
               [:div {:key (:question result)}
                [:h3 (:question result)]
                [chart
                 {:height 300
                  :width 800
                  :data (data-parse (count-percentage (:values result)))}
                 [interval
                  {:position "key*value"}]]]))])))