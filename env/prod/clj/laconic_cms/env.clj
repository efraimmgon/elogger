(ns laconic-cms.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[laconic-cms started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[laconic-cms has shut down successfully]=-"))
   :middleware identity})
