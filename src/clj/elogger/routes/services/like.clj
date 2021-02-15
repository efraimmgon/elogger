(ns elogger.routes.services.like
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.sql.like :as db]
    [elogger.db.sql.blog-post :as blog-post]
    [elogger.routes.services.common :as common]
    [ring.util.http-response :as response]))

(s/def :likes/id ::common/id)
(s/def :likes/user-id ::common/id)
(s/def :likes/created-at ::common/date)
(s/def :likes/updated-at ::common/date)
(s/def :likes/author :users/User)

(s/def :likes/Like
       (s/keys :req [:likes/id
                     :likes/user-id
                     :likes/created-at
                     :likes/updated-at
                     :likes/author]))

(s/def :likes/likes (s/* :likes/Like)) 

(s/def :threaded-comment/likes :likes/likes)
(s/def :blog-post/likes :likes/likes)

(defn author? [user-id like-id]
  (= user-id
     (-> like-id
         db/get-like-by-id
         :likes/user-id)))

(defn any-granted? [identity like-id]
  (when identity
    (or (:users/admin identity)
        (author? (:users/id identity) 
                 like-id))))

(defn delete! [comment-id]
  (db/delete! comment-id)
  (response/ok
    {:result :ok}))


(comment
  (require '[elogger.db.sql.common :as sqlcommon])
  (require '[elogger.db.pathom :refer [parser]])
  (s/valid?
    :likes/Like
    (parser {[:likes/id 1] sqlcommon/like-query}))
  (s/valid?
    :likes/likes
    (:likes/list
      (parser {[:likes/all] [{:likes/list sqlcommon/like-query}]})))

  :end)