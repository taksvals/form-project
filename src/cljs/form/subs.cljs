(ns form.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::data
 (fn [db]
   (:data db)))
