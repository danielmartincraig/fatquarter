(ns fat-quarter.views
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.subs :as subs]
   ))

(defn quilt-square [row column]
  [:rect {:width 20
          :height 20
          :x column
          :y row
          :fill "white"
          :stroke-width 1
          :stroke "black"
          :on-click #(js/alert (str row " " column))}])

(defn grid [dimensions]
  [:svg {:width 250
         :height 300}
    (for [column (map #(* 20 %) (range dimensions))
          row (map #(* 20 %) (range dimensions))]
      ^{:key (str row column)} [quilt-square row column])])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [grid 9]]))
