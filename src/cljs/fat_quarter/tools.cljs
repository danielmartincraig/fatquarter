(ns fat-quarter.tools
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.line-drawer :as line-drawer]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(def pointer
  (let [point (fn point [e column row] (js/alert "It's rude to point!"))]
    {:on-mouse-down point}))

(def clicker
  (let [click (fn click [e column row] (js/alert (str "You clicked " row "," column)))]
    {:on-mouse-down click}))

(re-frame/reg-event-db
 ::load-tools
 (re-frame/path [:toolbox :available-tools])
 (fn-traced []
            {:pointer {:attrs pointer
                       :state {}}
             :clicker {:attrs clicker
                       :state {}}
             :line-drawer line-drawer/line-drawer}))

