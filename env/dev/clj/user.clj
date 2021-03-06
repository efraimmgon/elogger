(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
    [elogger.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [elogger.figwheel :refer [start-fw stop-fw cljs]]
    [elogger.core :refer [start-app]]
    [elogger.db.core]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'elogger.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'elogger.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'elogger.db.core/*db*)
  (mount/start #'elogger.db.core/*db*)
  (binding [*ns* 'elogger.db.core]
    (conman/bind-connection elogger.db.core/*db* "sql/queries.sql")))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))


(comment
  (clojure.tools.namespace.repl/refresh)
  (reset-db)
  (create-migration "add-office-hours-table"))

