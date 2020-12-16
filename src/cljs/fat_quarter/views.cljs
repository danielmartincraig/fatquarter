(ns fat-quarter.views
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.subs :as subs]
   [fat-quarter.events :as events]
   ))

(defn interface-square [column row]
  (let [{:keys [on-click]} @(re-frame/subscribe [::subs/active-tool-attrs])
        on-click-fn #(apply on-click [column row])
        attrs {:width 20
               :height 20
               :x column
               :y row}]
    [:rect.interface-square (assoc attrs :on-click on-click-fn)]))

(defn interface []
  (let [quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])
        active-tool      @(re-frame/subscribe [::subs/active-tool])]
    [:g
     (doall
      (for [column (map #(* 20 %) (range (inc quilt-dimensions)))
            row (map #(* 20 %) (range (inc quilt-dimensions)))]
        ^{:key (str "interface-square," column "," row)} [interface-square column row]))]))

(defn graph []
  (let [quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])]
    [:g
     (for [column (map #(+ 10 (* 20 %)) (range quilt-dimensions))
           row (map #(+ 10 (* 20 %)) (range quilt-dimensions))]
       ^{:key (str "graph-square," column "," row )}
       [:rect.graph-square {:width 20
                            :height 20
                            :x column
                            :y row
                            }])]))

(defn quilt []
  (let [quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])
        quilt-paths      @(re-frame/subscribe [::subs/quilt-paths])]
    [:g
     ]))

(defn quilt-app []
  (let [quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])]
       [:svg {:width (* (inc quilt-dimensions) 20)
              :height (* (inc quilt-dimensions) 20)}
        [quilt]
        [graph]
        [interface]
        ]))

(defn toolbox-view []
  (let [active-tool @(re-frame/subscribe [::subs/active-tool])
        available-tools @(re-frame/subscribe [::subs/available-tools])]
    [:div.toolbox
     [:p (str "Active tool: " active-tool)]
     [:p (str "Available tools:")]
     [:ul (for [tool (keys available-tools)]
            ^{:key (str tool)} [:li {:on-click #(re-frame/dispatch [::events/set-active-tool tool])}
                         (str tool)])]]))


(defn dimension-buttons []
  [:div
   [:button {:on-click #(re-frame/dispatch [::events/increase-dimensions])} "+"]
   [:button {:on-click #(re-frame/dispatch [::events/decrease-dimensions])} "-"]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])]

    [:div
     [dimension-buttons]
     [toolbox-view]
     [quilt-app]
     ]))
