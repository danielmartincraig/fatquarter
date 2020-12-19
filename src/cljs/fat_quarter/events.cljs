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
 (fn-traced [active-tool [event new-active-tool & _]
             ]
            new-active-tool))

(re-frame/reg-event-db
 ::set-pen-down
 (re-frame/path [:pen-down])
 (fn-traced [pen-down & _]
            :down))

(re-frame/reg-event-db
 ::set-pen-up
 (re-frame/path [:pen-down])
 (fn-traced [pen-down & _]
            :up))

(re-frame/reg-event-db
 ::start-path
 (re-frame/path [:quilt :paths])
 (fn-traced [paths [event [column row] & _]]
            (conj paths [column row column row])))

(re-frame/reg-event-db
 ::continue-path
 (re-frame/path [:quilt :paths])
 (fn-traced [paths [event [column row] & _]]
            (let [last-line-idx (dec (count paths))]
              (update paths last-line-idx conj column row))))
