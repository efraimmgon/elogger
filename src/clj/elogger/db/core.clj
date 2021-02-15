(ns elogger.db.core
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [next.jdbc :as jdbc]
    [next.jdbc.date-time]
    [next.jdbc.prepare]
    [next.jdbc.result-set]
    [next.jdbc.sql]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [elogger.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import (org.postgresql.util PGobject)))

(defstate ^:dynamic *db*
  :start (if-let [jdbc-url (env :database-url)]
           (conman/connect! {:jdbc-url jdbc-url})
           (do
             (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
             *db*))
  :stop (conman/disconnect! *db*))

(defn pgobj->clj [^org.postgresql.util.PGobject pgobj]
  (let [type (.getType pgobj)
        value (.getValue pgobj)]
    (case type
      "json" (parse-string value true)
      "jsonb" (parse-string value true)
      "citext" (str value)
      value)))

(extend-protocol next.jdbc.result-set/ReadableColumn
  java.sql.Timestamp
  (read-column-by-label [^java.sql.Timestamp v _]
    (.toLocalDateTime v))
  (read-column-by-index [^java.sql.Timestamp v _2 _3]
    (.toLocalDateTime v))
  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]
    (.toLocalDate v))
  (read-column-by-index [^java.sql.Date v _2 _3]
    (.toLocalDate v))
  java.sql.Time
  (read-column-by-label [^java.sql.Time v _]
    (.toLocalTime v))
  (read-column-by-index [^java.sql.Time v _2 _3]
    (.toLocalTime v))
  java.sql.Array
  (read-column-by-label [^java.sql.Array v _]
    (vec (.getArray v)))
  (read-column-by-index [^java.sql.Array v _2 _3]
    (vec (.getArray v)))
  org.postgresql.util.PGobject
  (read-column-by-label [^org.postgresql.util.PGobject pgobj _]
    (pgobj->clj pgobj))
  (read-column-by-index [^org.postgresql.util.PGobject pgobj _2 _3]
    (pgobj->clj pgobj)))

(defn clj->jsonb-pgobj [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (generate-string value))))

(extend-protocol next.jdbc.prepare/SettableParameter
  clojure.lang.IPersistentMap
  (set-parameter [^clojure.lang.IPersistentMap v ^java.sql.PreparedStatement stmt ^long idx]
    (.setObject stmt idx (clj->jsonb-pgobj v)))
  clojure.lang.IPersistentVector
  (set-parameter [^clojure.lang.IPersistentVector v ^java.sql.PreparedStatement stmt ^long idx]
    (let [conn      (.getConnection stmt)
          meta      (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta idx)]
      (if-let [elem-type (when (= (first type-name) \_)
                           (apply str (rest type-name)))]
        (.setObject stmt idx (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt idx (clj->jsonb-pgobj v))))))

;;; ---------------------------------------------------------------------------
;;; Enhancing execute!

(def ->dash #(.replace % \_ \-))

(def ->underline #(.replace % \- \_))

(defn ->underline-key [k]
  (keyword 
    (when-let [n (namespace k)]
      (->underline n))
    (->underline (name k))))

(defn ->underline-keys [m]
  (->> m
       (map (fn [[k v]]
              [(->underline-key k) v]))
       (into {})))

(defn execute! [sql-params]
  (jdbc/execute! 
    *db*
    sql-params
    {:builder-fn next.jdbc.result-set/as-modified-maps
     :qualifier-fn ->dash
     :label-fn ->dash}))

(defn execute-one! [sql-params]
  (jdbc/execute-one! 
    *db*
    sql-params
    {:builder-fn next.jdbc.result-set/as-modified-maps
     :qualifier-fn ->dash
     :label-fn ->dash}))

(defn insert! [table params-map]
  (next.jdbc.sql/insert!
    *db*
    table
    (->underline-keys params-map)))

(defn insert-multi! [table columns values]
  (next.jdbc.sql/insert-multi!
    *db* table (mapv ->underline-key columns) values))

(defn update!
  "Given a table name, a hash map of columns and values to set,
  and either a hash map of columns and values to search on or a vector 
  of a SQL where clause and parameters, perform an update on the table."
  [table key-map where-params]
  (next.jdbc.sql/update!
    *db* 
    table 
    (->underline-keys key-map) 
    (if (map? where-params)
      (->underline-keys where-params)
      where-params)))

(defn delete! 
  "Given a table name, and either a hash map of columns 
  and values to search on or a vector of a SQL where clause and parameters, 
  perform a delete on the table."
  [table where-params]
  (next.jdbc.sql/delete!
    *db* 
    table 
    (if (map? where-params)
      (->underline-keys where-params)
      where-params)))

(comment
  (require '[next.jdbc.sql])
  
  (execute! 
    ["SELECT * FROM users"])
  
  (insert!
    "users"
    {:users/username "foo"
     :users/password "123456"
     :users/is-active false})
  
  (update!
    "users"
    {:users/username "bar"}
    {:users/username "foo"})

  (update!
    "users"
    {:users/username "bar"}
    ["username = ?" "foobar"])
  
  (delete!
    "users"
    {:users/id 3})
  
  (delete!
    "users"
    ["id = ?" 4])

  :end)