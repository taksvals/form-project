(ns form.components.pages.statistic.core
  (:require [reagent.core :as r]
            [bizcharts :refer [Chart Interval]]
            [re-frame.core :as re-frame]
            [form.events :as events]
            [form.subs :as subs]))

(def chart (r/adapt-react-class Chart))
(def interval (r/adapt-react-class Interval))

(defn data-parse [values]
  (into [] (for [[k v] values]
    (assoc (assoc {} :key k) :value v))))

(defn statistic-page []
  (re-frame/dispatch-sync [::events/load-results])
  (fn [] (let [results (:results @(re-frame/subscribe [::subs/results]))] 
           [:div.container
            [:div
             [:h1.title.center "Statistic"]]
            (for [result results]
              (when (> (reduce + (vals (:values result))) 0)
                [:div.chart-block {:key (:question result)}
                 [:h3.question.center (:question result)]
                 [chart
                  {:height 300
                   :width 650
                   :data (data-parse (:values result))}
                  [interval
                   {:position "key*value"}]]]))])))