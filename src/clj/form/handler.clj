(ns form.handler
  (:require
   [reitit.ring :as reitit-ring]
   [ring.middleware.json :as json]
   [form.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]
   [compojure.core :refer [defroutes GET POST]]
   [compojure.route :as route]
   [ring.util.http-response :as response]
   [cheshire.core :as ch]
   [clojure.data.json :as json-parse]))

(def results (atom {:title "About Yourself"
                    :results [{:question "How old are you?"
                               :values {}}
                              {:question "Date of birth"
                               :values {}}
                              {:question "What is your position1?"
                               :values
                               {:Teacher1 0
                                :Student1 0
                                :Administrator1 0
                                :Scientist1 0}}
                              {:question "What is your position2?"
                               :values
                               {:Teacher2 0
                                :Student2 3
                                :Administrator2 2
                                :Scientist2 0}}
                              {:question "What is your position3?"
                               :values
                               {:Teacher3 4
                                :Student3 3
                                :Administrator3 1
                                :Scientist3 3}}]}))

(defn update-results [answers]
  (let [all-results @results
        answers answers
        out {}]
    (reset! results (assoc
                     (assoc out :title (:title all-results))
                     :results
                     (into [] (for [question (range 0 (count (:results all-results)))
                                    :let [result (get (:results all-results) question)
                                          answer ((keyword (str (inc question))) answers)
                                          k (keyword (:answer answer))]]
                                (if (seq (:values result))
                                  (when k
                                    (assoc result :values
                                           (assoc (:values result)
                                                  k
                                                  (inc (k (:values result))))))
                                  result)))))))

(comment
  (update-results 
   {:1 {:answer "Tanya"}, 
    :2 {:answer "2021-07-29"}, 
    :3 {:answer "Administrator1"}, 
    :4 {:answer "Teacher2"}, :5 {:answer ["Teacher3", "Administrator3"]}}))

(def mount-target
  [:div#app
   [:h2 "Loading"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")
    [:script "form.core.init_BANG_()"]]))


(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn answers-handler [request]
  (prn (update-results (ch/generate-string (:json-params request))))
  (json-parse/write-str @results))

(defroutes handler
  (GET "/" [] index-handler)
  (GET "/statistic" [] index-handler)
  (GET "/questions" [] (slurp "resources/form.json"))
  (GET "/results" [] (json-parse/write-str @results))
  (POST "/answer" [] answers-handler)
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> handler
      (json/wrap-json-response)
      (json/wrap-json-params)))
