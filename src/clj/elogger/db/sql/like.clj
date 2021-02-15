(ns elogger.db.sql.like
  (:require
    [clojure.spec.alpha :as s]
    [elogger.db.core :as db]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as common]))


(defn get-like-by-id [id]
  (let [like (parser {[:likes/id id]
                      common/like-query})]
    (when (s/valid? :likes/Like like)
      like)))

(defn create! [user-id]
  (db/insert! "likes" {:user-id user-id}))
    
(defn delete! [id]
  (db/delete! "likes" {:id id}))