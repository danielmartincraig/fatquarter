(ns fat-quarter.zigzag-tool
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.subs]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.undo :as undo :refer [undoable]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;           Effects
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-event-fx
 ::start-path
 [(re-frame/path [:toolbox :available-tools :zigzag-tool :state])
  (undoable "start path")]
 (fn [{:keys [event db]} event]
   (let [[_ [column row]] event
         new-path [column row column row column row]]
     {:db (-> db
              (assoc :pen-down? true
                     :new-path new-path))})))

(re-frame/reg-event-db
 ::update-new-path-endpoint
 (re-frame/path [:toolbox :available-tools :zigzag-tool :state :new-path])
 (fn-traced [new-path [event [column row]]]
            (conj (pop (pop new-path)) column row)))

(re-frame/reg-event-fx
 ::end-path
 (re-frame/path [:toolbox :available-tools :zigzag-tool :state])
 (fn [{:keys [event db]} event]
   (let [[_ [column row]] event
         paths            (:paths db)
         new-path         (:new-path db)]
     {:db (-> db
              (assoc :pen-down? false
                     :paths     (conj paths
                                      (conj (pop (pop new-path)) column row))
                     :new-path []))})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;           Subscriptions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(re-frame/reg-sub
 ::zigzag-tool-state
 (fn [db]
   (get-in db [:toolbox :available-tools :zigzag-tool :state])))

(re-frame/reg-sub
 ::pen-down?
 :<- [::zigzag-tool-state]
 (fn [zigzag-tool-state]
   (:pen-down? zigzag-tool-state)))

(re-frame/reg-sub
 ::new-path
 :<- [::zigzag-tool-state]
 (fn [zigzag-tool-state]
   (:new-path zigzag-tool-state)))

(re-frame/reg-sub
 ::paths
 :<- [::zigzag-tool-state]
 (fn [zigzag-tool-state]
   (:paths zigzag-tool-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;           Tool Definition
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def zigzag-tool
  (let [pen-down? (re-frame/subscribe [::pen-down?])
        new-path (re-frame/subscribe [::new-path])
        start-path (fn start-path [e column row]
                     (if (not= (.-buttons e) 0)
                       (do
                         (re-frame/dispatch-sync [::start-path [column row]]))))
        continue-path (fn [e column row]
                        (when @pen-down?
                          (re-frame/dispatch-sync [::update-new-path-endpoint [column row]])))
        end-path (fn end-path [e column row]
                   (when @pen-down?
                     (do
                       (re-frame/dispatch-sync [::end-path [column row]]))))]
    {:attrs {:on-mouse-down start-path
             :on-mouse-over start-path
             :on-mouse-move continue-path
             :on-mouse-up end-path}
     :state {:pen-down? false
             :paths []
             :new-path []}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;           View Definition
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn zigzag-tool-layer []
  (let [new-path (re-frame/subscribe [::new-path])
        paths    (re-frame/subscribe [::paths])]
    (fn []
      [:g
       (for [[x y & more-points] (conj @paths @new-path)]
         ^{:key (str "zigzag-tool-path," x y more-points)}
         [:path.zigzag-path {:d (str "M " x " " y " L " (clojure.string/join " " more-points))}])])))

