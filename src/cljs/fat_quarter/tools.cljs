(ns fat-quarter.tools
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.events :as events]
   [fat-quarter.subs :as subs]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(def pointer
  (let [point (fn point [e column row] (js/alert "It's rude to point!"))]
    {:on-mouse-down point}))

(def clicker
  (let [click (fn click [e column row] (js/alert (str "You clicked " row "," column)))]
    {:on-mouse-down click}))

(def line-drawer
  (let [pen-down @(re-frame/subscribe [::subs/pen-down])
        paths    @(re-frame/subscribe [::subs/quilt-paths])
        start-path (fn start-path [e column row]
                     (let [pen-down @(re-frame/subscribe [::subs/pen-down])]
                       (re-frame/console :log (str "setting pen down, was " pen-down))
                       (when (not= (.-buttons e) 0)
                         (re-frame/console :log (str "e " e))
                         (re-frame/dispatch [::events/set-pen-down])
                         (re-frame/dispatch [::events/start-path [column row]]))))
        continue-path (fn continue-path [e column row]
                        (let [pen-down @(re-frame/subscribe [::subs/pen-down])]
                          (when (= :down pen-down)
                            (re-frame/console :log (str "continuing path"))
                            (re-frame/dispatch [::events/continue-path [column row]]))))
        end-path (fn end-path [e column row]
                   (let [pen-down @(re-frame/subscribe [::subs/pen-down])]
                     (re-frame/console :log (str "setting pen up, was " pen-down))
                     (when (= :down pen-down)
                       (continue-path e column row)
                       (re-frame/dispatch-sync [::events/set-pen-up]))))
        ]
    {
     :on-mouse-down start-path
     :on-mouse-over start-path
     :on-mouse-move continue-path
     :on-mouse-up end-path
     :on-mouse-out end-path
     }))

(re-frame/reg-event-db
 ::load-tools
 (re-frame/path [:toolbox :available-tools])
 (fn-traced []
            {:pointer pointer
             :clicker clicker
             :line-drawer line-drawer
             }))


