(ns elogger.db.sql.threaded-comment
  (:require
    [clojure.spec.alpha :as s]
    [elogger.db.core :as db :refer [*db*]]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as common]
    [elogger.db.sql.like :as like]
    [next.jdbc :as jdbc]))

    
(defn get-comment-by-id [id]
  (let [comment (parser {[:threaded-comment/id id]
                         common/threaded-comment-query})]
    (when (s/valid? :threaded-comment/Comment comment)
      comment)))

(defn get-comments []
  (:threaded-comments/list
    (parser {[:threaded-comments/all]
             [{:threaded-comments/list common/threaded-comment-query}]})))
    
(defn create-comment!
  "Create a comment record."
  [params]
  (db/insert! "threaded_comment" params))

(defn update-comment!
  "Update threaded-comment record that matches id."
  [params]
  (db/update! "threaded_comment" 
              (dissoc params :threaded-comment/id)
              (select-keys params [:threaded-comment/id])))

(defn approve-comment!
  "Sets the :is-approved field on the threaded-comment record to true."
  [id]
  (update-comment! {:threaded-comment/id id,
                    :threaded-comment/is-approved true}))

(defn unapprove-comment!
  "Sets the :is-approved field on the threaded-comment record to false."
  [id]
  (update-comment! {:threaded-comment/id id,
                    :threaded-comment/is-approved false}))

(defn delete-comment!
  "Sets the :is-deleted field on the threaded-comment record to true."
  [id]
  (update-comment! {:threaded-comment/id id,
                    :threaded-comment/is-deleted true}))

;;; ---------------------------------------------------------------------------
;;; Like

(defn create-comment-like! 
  "Creates a comment-likes record relation."
  [params]
  (db/insert! "comment_likes" params))

(defn like! 
  "Takes a id and a user-id and creates a like record for that
  comment."
  [params]
  (jdbc/with-transaction [tx *db*]
    (binding [db/*db* tx]
      (let [like-id (-> (like/create! (:users/id params))
                        :likes/id)]
        (create-comment-like!
          {:like-id like-id
           :comment-id (:threaded-comment/id params)})))))

;;; ---------------------------------------------------------------------------
;;; Resolvers


; We don't expect to delete comments. The comments table has a is-deleted
; field which will be set to true if the comment is "deleted".
(comment
  (defn delete-comment!
    "Delete a comment record"
    [id]
    (db/execute!
      {:delete-from :threaded-comment
       :where [:= :id id]}))

  (db/parser
    [{[:threaded-comment/by-id 1] 
      common/comment-query}])

  (db/parser
    [{[:threaded-comment/by-id 1]
      [:threaded-comment/id
       {:threaded-comment/likes common/like-columns}]}]))