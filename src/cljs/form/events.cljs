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
                 :on-success [:success-questions]
                 :on-failure [:failure-questions]}}))

(re-frame/reg-event-fx
 ::load-results
;;  [(re-frame/inject-cofx :now)]
 (fn [{:keys [db] :as cofx} _]
   {:http-xhrio {:uri "http://localhost:3000/results"
                 :method :get
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:success-results]
                 :on-failure [:failure-results]}}))

(re-frame/reg-event-fx
 ::update-results
;;  [(re-frame/inject-cofx :now)]
 (fn [_cofx [_ answers]]
   {:http-xhrio {:uri "http://localhost:3000/answer"
                 :method :post
                 :params answers
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:process-post-response]
                 :on-failure [:bad-post-response]}}))

(re-frame/reg-event-db
  :success-questions
  (fn [db [_ response]]
    (assoc db :data (js->clj response))))

(re-frame/reg-event-db
  :failure-questions
  (fn [_ [_ response]]
    (println response)))

(re-frame/reg-event-db
  :success-results
  (fn [db [_ response]]
    (assoc db :results (js->clj response))))

(re-frame/reg-event-db
  :failure-results
  (fn [_ [_ response]]
    (println response)))

(re-frame/reg-event-db
  :process-post-response
  (fn [db [_ response]]
    (println "Success " response)))

(re-frame/reg-event-db
  :bad-post-response
  (fn [_ [_ response]]
    (println "Failure " response)))
