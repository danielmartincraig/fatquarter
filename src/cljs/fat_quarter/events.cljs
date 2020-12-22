(ns fat-quarter.events
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.undo :as undo :refer [undoable]]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::increase-dimensions
 (re-frame/path [:quilt :dimensions])
 (fn-traced [b [event & _]]
            (inc b)))

(re-frame/reg-event-db
 ::decrease-dimensions
 (re-frame/path [:quilt :dimensions])
 (fn-traced [b [event & _]]
            (dec b)))

(re-frame/reg-event-db
 ::set-dimensions
 (re-frame/path [:quilt :dimensions])
 (fn-traced [event]
            (re-frame/console :log (str event))))

(re-frame/reg-event-db
 ::set-active-tool
 (re-frame/path [:active-tool])
 (fn-traced [active-tool [event new-active-tool & _]
             ]
            new-active-tool))

(re-frame/reg-event-db
 ::add-new-path
 [(re-frame/path [:quilt :paths])
  (undoable "adding path")]
 (fn-traced [paths [event new-path & _]]
            (conj paths new-path)))

