(ns fat-quarter.views
  (:require
   [re-frame.core :as re-frame]
   [fat-quarter.subs :as subs]
   [fat-quarter.events :as events]
   [fat-quarter.line-drawer :as line-drawer]))

(defn interface-square [column row]
  (let [{:keys [on-mouse-down on-mouse-up on-mouse-over on-mouse-out on-mouse-move]} @(re-frame/subscribe [::subs/active-tool-attrs])
        tool-state (re-frame/subscribe [::subs/active-tool-state])
        vx (+ 10 column)
        vy (+ 10 row)
        on-mouse-down-fn (if on-mouse-down (fn [e] (apply on-mouse-down [e vx vy])) nil)
        on-mouse-up-fn   (if on-mouse-up   (fn [e] (apply on-mouse-up   [e vx vy])) nil)
        on-mouse-over-fn (if on-mouse-over (fn [e] (apply on-mouse-over [e vx vy])) nil)
        on-mouse-out-fn  (if on-mouse-out (fn [e] (apply on-mouse-out [e vx vy])) nil)
        on-mouse-move-fn  (if on-mouse-move (fn [e] (apply on-mouse-move [e vx vy])) nil)
        attrs {:width 20
               :height 20
               :x column
               :y row}]
    (fn []
      [:rect.interface-square (assoc attrs :on-mouse-down on-mouse-down-fn
                                     :on-mouse-up         on-mouse-up-fn
;;                                     :on-mouse-over       on-mouse-over-fn
;;                                     :on-mouse-out        on-mouse-out-fn
                                     :on-mouse-move       on-mouse-move-fn)])))

(defn interface []
  (let [quilt-dimensions (re-frame/subscribe [::subs/quilt-dimensions])]
    (fn []
      [:g
       (doall
        (for [column (map #(* 20 %) (range (inc @quilt-dimensions)))
              row (map #(* 20 %) (range (inc @quilt-dimensions)))]
          ^{:key (str "interface-square," column "," row)} [interface-square column row]))])))

(defn graph []
  (let [quilt-dimensions (re-frame/subscribe [::subs/quilt-dimensions])]
    (fn []
      [:g
       (doall
        (for [column (map #(+ 10 (* 20 %)) (range @quilt-dimensions))
              row (map #(+ 10 (* 20 %)) (range @quilt-dimensions))]
          ^{:key (str "graph-square," column "," row )}
          [:rect.graph-square {:width 20
                               :height 20
                               :x column
                               :y row}]))])))

(defn quilt []
  (let [quilt-paths (re-frame/subscribe [::subs/quilt-paths])]
    (fn []
      [:g
       (for [[x y & more-points] @quilt-paths]
         ^{:key (str "quilt-path," x y more-points)}
         [:path.quilt-path {:d (str "M " x " " y " L " (clojure.string/join " " more-points))}])])))

(defn quilt-app []
  (let [quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])]
    (fn []
      [:svg {:width (* (inc quilt-dimensions) 20)
             :height (* (inc quilt-dimensions) 20)}
       [graph]
       [quilt]
       [line-drawer/line-drawer-layer]
       [interface]])))

(defn undo-button []
  (let [undos? (re-frame/subscribe [:undos?])]
    (fn []
      [:input {:type "button"
               :value "undo"
               :disabled (not @undos?)
               :on-click #(re-frame/dispatch [:undo])}])))

(defn redo-button []
  (let [redos? (re-frame/subscribe [:redos?])]
    (fn []
      [:input {:type "button"
               :value "redo"
               :disabled (not @redos?)
               :on-click #(re-frame/dispatch [:redo])}])))

(defn toolbox-view []
  (let [active-tool @(re-frame/subscribe [::subs/active-tool])
        available-tools @(re-frame/subscribe [::subs/available-tools])]
    (fn []
      [:div.toolbox
       [:p (str "Active tool: " active-tool)]
       [:p (str "Available tools:")]
       [:ul (for [tool (keys available-tools)]
              ^{:key (str tool)} [:li {:on-click #(re-frame/dispatch [::events/set-active-tool tool])}
                                  (str tool)])]])))

(defn buttons []
  (fn []
    [:div
     [:div.dimension-buttons
      [:button {:on-click #(re-frame/dispatch [::events/increase-dimensions])} "+"]
      [:button {:on-click #(re-frame/dispatch [::events/decrease-dimensions])} "-"]]
     [:div.undo-redo-buttons
      [undo-button]
      [redo-button]]]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        quilt-dimensions @(re-frame/subscribe [::subs/quilt-dimensions])]

    [:div
     [buttons]
;;     [toolbox-view]
     [quilt-app]
     ]))
