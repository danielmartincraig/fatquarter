(ns fat-quarter.tools
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.events :as events]
   [fat-quarter.subs :as subs]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(def pointer
  (let [point (fn [] (js/alert "It's rude to point!"))]
    {:on-click point}))

(def clicker
  (let [click (fn [row column] (js/alert (str "You clicked " row "," column)))]
    {:on-click click}))

(defn line-drawer []
  (let [pen-down? @(re-frame/subscribe [::subs/pen-down?])
        paths     @(re-frame/subscribe [::subs/paths])
        xy         (fn xy [e]
                     (let [rect (.getBoundingClientRect (.-target e))]
                       [(- (.-clientX e) (.-left rect))
                        (- (.-clientY e) (.-top rect))]))
        start-path (fn start-path [e] (when (not= (.-buttons e) 0)
                                        (re-frame/dispatch [::events/set-pen-down])
                                        (let [[x y] (xy e)]
                                          (re-frame/dispatch [::events/start-path [x y]]))))
        continue-path (fn continue-path [e] (when pen-down?
                                              (let [[x y] (xy e)]
                                                (re-frame/dispatch [::events/continue-path [x y]]))))
        end-path (fn end-path [e]
                   (when pen-down?
                     (continue-path e)
                     (re-frame/dispatch [::events/set-pen-up])))
        ]))

(re-frame/reg-event-db
 ::load-tools
 (re-frame/path [:toolbox :available-tools])
 (fn-traced []
            {:pointer pointer
             :clicker clicker
             ;;             :line-drawer line-drawer
             }))


