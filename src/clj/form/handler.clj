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
   [clojure.data.json :as json-parse]
   
   [form.update-result :refer [update-results results]]))

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
  {:code 200 
   :body (update-results (ch/parse-string
                   (ch/generate-string (:json-params request)) true))})

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
