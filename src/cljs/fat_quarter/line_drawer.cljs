(ns fat-quarter.line-drawer
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.subs]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.undo :as undo :refer [undoable]]))

(re-frame/reg-event-db
 ::add-new-path
 [(re-frame/path [:quilt :paths])
  (undoable "adding path")]
 (fn-traced [paths [event new-path & _]]
            (conj paths new-path)))

(re-frame/reg-event-db
 ::set-pen-up
 (re-frame/path [:toolbox :available-tools :line-drawer :state :pen-down?])
 (fn-traced [] false))

(re-frame/reg-event-db
 ::set-pen-down
 (re-frame/path [:toolbox :available-tools :line-drawer :state :pen-down?])
 (fn-traced [] true))

(re-frame/reg-event-db
 ::start-new-path
 (re-frame/path [:toolbox :available-tools :line-drawer :state :new-path])
 (fn-traced [_ [event new-path]]
            new-path))

(re-frame/reg-event-db
 ::finish-new-path
 (re-frame/path [:toolbox :available-tools :line-drawer :state :new-path])
 (fn-traced [new-path [event [row column]]]
            (conj new-path row column)))

(re-frame/reg-event-db
 ::clear-new-path
 (re-frame/path [:toolbox :available-tools :line-drawer :state :new-path])
 (fn-traced [_]
            []))

(re-frame/reg-sub
 ::pen-down?
 :<- [:fat-quarter.subs/active-tool-state]
 (fn [active-tool-state]
   (:pen-down? active-tool-state)
   ))

(re-frame/reg-sub
 ::new-path
 :<- [:fat-quarter.subs/active-tool-state]
 (fn [active-tool-state]
   (:new-path active-tool-state)))

(def line-drawer
  (let [pen-down? (re-frame/subscribe [::pen-down?])
        new-path (re-frame/subscribe [::new-path])
        start-path (fn start-path [e column row]
                     (when (not= (.-buttons e) 0)
                       (re-frame/dispatch-sync [::set-pen-down])
                       (re-frame/dispatch-sync [::start-new-path [column row column row]])))
        end-path (fn end-path [e column row]
                   (when @pen-down?
                     (re-frame/dispatch-sync [::set-pen-up])
                     (re-frame/dispatch-sync [::finish-new-path [column row]])
                     (re-frame/dispatch-sync [::add-new-path @new-path])
                     (re-frame/dispatch-sync [::clear-new-path])))]
    {:attrs {:on-mouse-down start-path
             :on-mouse-over start-path
             :on-mouse-up end-path
             :on-mouse-out end-path}
     :state {:pen-down? false
             :new-path []}}))


