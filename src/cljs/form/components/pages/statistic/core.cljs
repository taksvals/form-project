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

(defn statistic-page []
  (re-frame/dispatch-sync [::events/load-results])
  (fn [] (let [results (:results @(re-frame/subscribe [::subs/data]))] 
          [:div
           [:span.main
            [:h1 "Statistic"]]
           (for [result results]
             (when (seq (:values result))
               [:div
                [:h3 (:question result)]
                [chart
                 {:height 300
                  :width 800
                  :data (data-parse (:values result))}
                 [interval
                  {:position "key*value"}]]]))])))