(ns elogger.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [elogger.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[elogger started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[elogger has shut down successfully]=-"))
   :middleware wrap-dev})
