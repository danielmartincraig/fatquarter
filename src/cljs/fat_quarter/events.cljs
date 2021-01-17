(ns fat-quarter.events
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
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
 (fn-traced [active-tool [event new-active-tool & _]]
            new-active-tool))



