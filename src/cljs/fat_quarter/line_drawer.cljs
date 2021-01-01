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
 (fn-traced [_ [event [column row]]]
            [column row column row column row]))

(re-frame/reg-event-db
 ::finish-new-path
 (re-frame/path [:toolbox :available-tools :line-drawer :state :new-path])
 (fn-traced [new-path [event [column row]]]
            (conj new-path column row)))

(re-frame/reg-event-db
 ::clear-new-path
 (re-frame/path [:toolbox :available-tools :line-drawer :state :new-path])
 (fn-traced [_]
            []))

(re-frame/reg-event-db
 ::update-new-path-endpoint
 (re-frame/path [:toolbox :available-tools :line-drawer :state :new-path])
 (fn-traced [new-path [event [column row]]]
            (conj (pop (pop new-path)) column row)))

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
                     (if (not= (.-buttons e) 0)
                       (do
                         (re-frame/dispatch-sync [::set-pen-down])
                         (re-frame/dispatch-sync [::start-new-path [column row]]))))
        continue-path (fn [e column row]
                        (when @pen-down?
                          (re-frame/dispatch-sync [::update-new-path-endpoint [column row]])))
        end-path (fn end-path [e column row]
                   (when @pen-down?
                     (do
                      (re-frame/dispatch-sync [::set-pen-up])
                      (re-frame/dispatch-sync [::update-new-path-endpoint [column row]])
                      (re-frame/dispatch-sync [::add-new-path @new-path])
                      (re-frame/dispatch-sync [::clear-new-path]))
                     ))]
    {:attrs {:on-mouse-down start-path
             :on-mouse-over start-path
             :on-mouse-move continue-path
             :on-mouse-up end-path}

     :state {:pen-down? false
             :new-path []}}))

(defn line-drawer-layer []
  (let [new-path (re-frame/subscribe [::new-path])]
    (fn []
      (let [[x y & more-points] @new-path]
        [:path.new-path {:d (str "M " x " " y " L " (clojure.string/join " " more-points))}]
        ))))

