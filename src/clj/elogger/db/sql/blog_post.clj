(ns elogger.db.sql.blog-post
  (:require 
    [clojure.spec.alpha :as s]
    [elogger.db.core :as db]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.blog-post-comment :as bpc]
    [elogger.db.sql.common :as common]
    [elogger.db.sql.like :as like]
    [elogger.db.sql.threaded-comment :as comment]
    [next.jdbc :as jdbc]))

(defn get-post-by-id [id]
  (let [post (parser {[:blog-post/id id] common/blog-post-query})]
    (when (s/valid? :blog-post/BlogPost post)
      post)))

(defn get-posts []
  (:blog-posts/list
    (parser {[:blog-posts/all] 
             [{:blog-posts/list common/blog-post-query}]})))

(defn get-blog-posts-comments []
  (->>
    (parser {[:blog-posts/all]
             [{:blog-posts/list common/blog-post-comment-query}]})
    :blog-posts/list
    (filter :blog-post/comments)))

(defn get-blog-post-comments [blog-post-id]
  (parser {[:blog-post/id blog-post-id]
           common/blog-post-comment-query}))

(defn create-post!
  "Creates a new blog post record."
  [params]
  (db/insert! "blog_post" params))

(defn create-comment!
  "Creates a comment record for the given blog post."
  [{:keys [blog-post/id] :as params}]
  (jdbc/with-transaction [tx db/*db*]
    (binding [db/*db* tx]
      (let [ret (comment/create-comment! 
                  (select-keys params common/threaded-comment-columns))]
        (bpc/create!
          {:threaded-comment/id (:threaded_comment/id ret)
           :blog-post/id id})))))

(defn update-post!
  "Updates an existing blog post record, by id."
  [{:keys [blog-post/id] :as params}]
  (db/update! "blog_post"
              (dissoc params :blog-post/id)
              (select-keys params [:blog-post/id])))

  
(defn delete-post!
  "Deletes a blog-post record given the id."
  [id]
  (db/delete! "blog_post" {:id id}))

;;; ---------------------------------------------------------------------------
;;; Like


(defn create-blog-post-like! 
  "Create a record in the blog_post_like relation."
  [params]
  (db/insert! "blog_post_likes" params))

(defn like!
  "Takes a blog-post-id and a user id and creates a like record for the
  blog post."
  [params]
  (jdbc/with-transaction [tx db/*db*]
    (binding [db/*db* tx]
      (create-blog-post-like!
        {:like-id (-> (:users/id params)
                      like/create!
                      :likes/id)
         :blog-post-id (:blog-post/id params)}))))



(comment
  (take 5 (get-all-comments))
  
  (:blog-post/likes (get-post 1))
  
  (like! {:blog-post/id 1
          :users/user-id 1})


  (-> (get-post 2)
      :blog-post/likes)
  
  (s/explain
    :blog-post/BlogPost
    (parser {[:blog-post/id id] common/blog-post-query})) 
  
  
  (db/parser-print
    [{[:blog-posts/all] 
      [:blog-post/id]}]))
