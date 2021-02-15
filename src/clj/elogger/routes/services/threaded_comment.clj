(ns elogger.routes.services.threaded-comment
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.sql.threaded-comment :as db]
    [elogger.db.sql.blog-post :as blog-post]
    [elogger.routes.services.common :as common]
    [ring.util.http-response :as response]))

;;; ---------------------------------------------------------------------------
;;; DOMAIN

(s/def :threaded-comment/id ::common/id)
(s/def :threaded-comment/author-id (s/nilable :users/id))
(s/def :threaded-comment/author-name (s/nilable string?))
(s/def :threaded-comment/title string?)
(s/def :threaded-comment/body string?)
(s/def :threaded-comment/parent (s/nilable :threaded-comment/id))
(s/def :threaded-comment/is-approved boolean?)
(s/def :threaded-comment/is-deleted boolean?)
(s/def :threaded-comment/created-at ::common/date)
(s/def :threaded-comment/updated-at ::common/date)
(s/def :threaded-comment/author (s/nilable :users/User))

(s/def :threaded-comment/Comment
       (s/keys :req  [:threaded-comment/id
                      :threaded-comment/author-id
                      :threaded-comment/author-name
                      :threaded-comment/title
                      :threaded-comment/body
                      :threaded-comment/parent
                      :threaded-comment/is-approved
                      :threaded-comment/is-deleted
                      :threaded-comment/created-at
                      :threaded-comment/updated-at]
               :opt  [:threaded-comment/likes
                      :threaded-comment/author]))

(s/def :threaded-comment/comments (s/* :threaded-comment/Comment))

;;; ---------------------------------------------------------------------------
;;; CORE

(defn author? [user-id comment-id]
  (= user-id
     (-> comment-id
         db/get-comment-by-id
         :threaded-comment/author-id)))

(defn any-granted? [identity comment-id]
  (when identity
    (or (:users/admin identity)
        (author? (:users/id identity) 
                 comment-id))))


(defn create-comment!
  "Create a comment record."
  [params]
  (db/create-comment! params)
  (response/ok
    {:result :ok}))

(defn get-comment
  "Get a comment record by id."
  [id]
  (response/ok
    (db/get-comment-by-id id)))

(defn get-comments
  "Get all comment records."
  []
  (response/ok
    (db/get-comments)))

(defn update-comment!
  "Update a comment record by id."
  [params]
  (db/update-comment! params)
  (response/ok
    {:result :ok}))

(defn delete-comment!
  "Sets the comment record is-deleted to true."
  [id]
  (db/update-comment!
    {:id id
     :is-deleted true})
  (response/ok
    {:result :ok}))


(defn like!
  [params]
  (db/like! params)
  (response/ok
    {:result :ok}))
