(ns fat-quarter.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [fat-quarter.events :as events]
   [fat-quarter.views :as views]
   [fat-quarter.config :as config]
   [fat-quarter.tools :as tools]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::tools/load-tools])
  (dev-setup)
  (mount-root))
