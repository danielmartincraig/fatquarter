(ns fat-quarter.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::quilt
 (fn [db]
   (:quilt db)))

(re-frame/reg-sub
 ::toolbox
 (fn [db]
   (:toolbox db)))

(re-frame/reg-sub
 ::active-tool
 (fn [db _]
   (:active-tool db)))

(re-frame/reg-sub
 ::quilt-dimensions
 :<- [::quilt]
 (fn [quilt _]
   (:dimensions quilt)))

(re-frame/reg-sub
 ::quilt-paths
 :<- [::quilt]
 (fn [quilt _]
   (:paths quilt)))

(re-frame/reg-sub
 ::available-tools
 :<- [::toolbox]
 (fn [toolbox _]
   (:available-tools toolbox)))

(re-frame/reg-sub
 ::active-tool-attrs
 :<- [::available-tools]
 :<- [::active-tool]
 (fn [[available-tools active-tool] _]
   (get available-tools active-tool)))

