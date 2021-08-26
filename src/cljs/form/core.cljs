(ns form.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [re-frame.core :as re-frame]
   [form.components.pages.home.core :refer [home-page]]
   [form.components.pages.statistic.core :refer [statistic-page]]
   [form.events :as events]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/statistic" :statistic]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :statistic #'statistic-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header.nav
        [:ul
         [:li [:a {:href (path-for :index)} "Home"]]
         [:li [:a {:href (path-for :statistic)} "Statistic"]]]]
       [page]])))

;; -------------------------
;; Initialize app

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [current-page] root-el)))

(defn init! []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/load-questions])
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
