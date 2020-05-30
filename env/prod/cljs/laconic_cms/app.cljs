(ns laconic-cms.app
  (:require [laconic-cms.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
