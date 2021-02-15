(ns elogger.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[elogger started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[elogger has shut down successfully]=-"))
   :middleware identity})
