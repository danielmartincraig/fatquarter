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
  (let [pen-down? (reagent.ratom/atom :up)
        new-path (reagent.ratom/atom [])
        paths    @(re-frame/subscribe [::subs/quilt-paths])
        start-path (fn start-path [e column row]
                     (re-frame/console :log (str "setting pen down, was " @pen-down?))
                     (when (not= (.-buttons e) 0)
                       (reset! pen-down? :down)
                       (reset! new-path [column row column row])))
        end-path (fn end-path [e column row]
                   (re-frame/console :log (str "setting pen up, was " @pen-down?))
                   (when (= :down @pen-down?)
                     #_(swap! new-path conj column row)
                     (reset! pen-down? :up)
                     (re-frame/dispatch-sync [::events/add-new-path (conj @new-path column row)])))
        ]
    {
     :on-mouse-down start-path
     :on-mouse-over start-path
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


