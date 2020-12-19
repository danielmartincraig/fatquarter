(ns fat-quarter.db
  (:require
   [re-frame.core :as re-frame]))

(def default-db
  {:quilt {:dimensions 10
           :paths []}
   :active-tool :line-drawer
   :toolbox {:available-tools {}}
   :pen-down :up})
