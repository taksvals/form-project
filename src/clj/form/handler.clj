(ns form.handler
  (:require
   [reitit.ring :as reitit-ring]
   [ring.middleware.json :as json]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]
   [compojure.core :refer [defroutes GET POST]]
   [compojure.route :as route]
   [ring.util.http-response :as response]
   [cheshire.core :as ch]
   [clojure.data.json :as json-parse]
   
   [form.update-result :refer [update-results results
                               update-results-statistic]]))

(def mount-target
  [:div#app
   [:div.center
    [:h1.title "Loading..."]
    [:img.loading-img {:src "/gifs/loading.gif"}]]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   [:title "Form"]
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
  {:status 200 
   :body (update-results (ch/parse-string
                   (ch/generate-string (:json-params request)) true))})

(defroutes handler
  (GET "/" [] index-handler)
  (GET "/statistic" [] )
  (GET "/questions" [] (slurp "resources/form.json"))
  (GET "/results" [] (json-parse/write-str (update-results-statistic @results)))
  (POST "/answer" [] answers-handler)
  (route/not-found "<h1>Oops.. Page not found</h1>"))

(def app
  (-> handler
      (json/wrap-json-response)
      (json/wrap-json-params)))
