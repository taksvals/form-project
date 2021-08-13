(ns form.events
  (:require
   [re-frame.core :as re-frame]
   [form.db :as db]
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::load-questions
;;  [(re-frame/inject-cofx :now)]
 (fn [{:keys [db] :as cofx} _]
   {:http-xhrio {:uri "http://localhost:3000/questions"
                 :method :get
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:process-response]
                 :on-failure [:bad-response]}}))

(re-frame/reg-event-fx
 ::load-results
;;  [(re-frame/inject-cofx :now)]
 (fn [{:keys [db] :as cofx} _]
   {:http-xhrio {:uri "http://localhost:3000/results"
                 :method :get
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:process-response]
                 :on-failure [:bad-response]}}))

(re-frame/reg-event-db
  :process-response
  (fn [db [_ response]]
    (assoc db :data (js->clj response))))

(re-frame/reg-event-db
  :bad-response
  (fn [_ [_ response]]
    (println response)))
