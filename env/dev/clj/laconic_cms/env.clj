(ns laconic-cms.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [laconic-cms.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[laconic-cms started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[laconic-cms has shut down successfully]=-"))
   :middleware wrap-dev})
