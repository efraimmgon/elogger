(ns elogger.routes.services.blog-post
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.sql.blog-post :as db]
    [elogger.db.sql.user :refer [get-user]]
    [elogger.routes.services.common :as common]
    ;; Require their specs
    [elogger.routes.services.threaded-comment]
    [elogger.routes.services.user]
    [elogger.utils :refer [string->date]]
    [ring.util.http-response :as response]))

;;; ---------------------------------------------------------------------------
;;; DOMAIN

(s/def :blog-post/id ::common/id)
(s/def :blog-post/title string?)
(s/def :blog-post/subtitle (s/nilable string?))
(s/def :blog-post/content string?)
(s/def :blog-post/author-id int?)
(s/def :blog-post/featured-img-id (s/nilable ::common/id))
(s/def :blog-post/published-at ::common/date)
(s/def :blog-post/expires-at (s/nilable ::common/date))
(s/def :blog-post/status ::common/status)
(s/def :blog-post/created-at ::common/date)
(s/def :blog-post/updated-at ::common/date)

(s/def :blog-post/author :users/User)

; The ≠ between BlotPostRaw and BlogPost is the later also includes
; the joins.

(s/def :blog-post/BlogPostRaw
       (s/keys :req  [:blog-post/id
                      :blog-post/title
                      :blog-post/subtitle
                      :blog-post/content
                      :blog-post/author-id
                      :blog-post/featured-img-id
                      :blog-post/published-at
                      :blog-post/expires-at
                      :blog-post/status
                      :blog-post/created-at
                      :blog-post/updated-at]))
(s/def :blog-post/blog-posts-raw (s/* :blog-post/BlogPostRaw))

(s/def :blog-post/BlogPost
       (s/keys :req  [:blog-post/id
                      :blog-post/title
                      :blog-post/subtitle
                      :blog-post/content
                      :blog-post/author-id
                      :blog-post/author
                      :blog-post/featured-img-id
                      :blog-post/published-at
                      :blog-post/expires-at
                      :blog-post/status
                      :blog-post/created-at
                      :blog-post/updated-at]
               :opt  [:blog-post/likes
                      :blog-post/comments]))

(s/def :blog-post/blog-posts (s/* :blog-post/BlogPost))

;;; Blog-post - Comments

(s/def :blog-post/comments :threaded-comment/comments)

(s/def :blog-post/comments-for-blog-post
       (s/keys :req [:blog-post/id
                     :blog-post/comments]))

(s/def :blog-post/all-comments
       (s/* :blog-post/comments-for-blog-post))

;;; ---------------------------------------------------------------------------
;;; CORE

(defn author? [user-id post-id]
  (= user-id
     (-> post-id
         db/get-post-by-id
         :blog-post/author-id)))

(defn any-granted? [identity blog-post-id]
  (when identity
     (or (:users/admin identity)
         (author? (:users/id identity) blog-post-id))))

(defn get-post
  "Get a blog post record by id."
  [id]
  (response/ok
    (db/get-post-by-id id)))
          
(defn get-posts
  "Get all blog post records."
  []
  (response/ok
    (db/get-posts)))

(defn get-comments-for
  "Get all comment records for a blog post by id."
  [id]
  (response/ok
    (db/get-blog-post-comments id)))

(defn get-all-comments 
  "Get all blog-post's comment records."
  []
  (response/ok
    (db/get-blog-posts-comments)))

(defn create-post!
  "Create a blog post record."
  [params]
  ;; TODO: check that image file exists:
  ; (file-db/get-file {:file-id (:featured-img-id params)})
  (db/create-post! 
    (-> params
        (update :blog-post/published-at string->date)
        (update :blog-post/expires-at string->date)))
  (response/ok
    {:result :ok}))

(defn create-comment!
  "Create a comment record for a blog post by id."
  [params]
  (db/create-comment! params)
  (response/ok
    {:result :ok}))

(defn update-post!
  "Update a blog post record by id."
  [params]
  ;; TODO: check that image file exists:
  ; (file-db/get-file {:file-id (:featured-img-id params)})
  (db/update-post!
    (-> params
        ; We don't want to overwrite the author, in case it's a different user
        (dissoc :users/id :blog-post/created-at) 
        (assoc :blog-post/updated-at (common/now))
        (update :blog-post/published-at string->date)
        (update :blog-post/expires-at string->date)))
          
  (response/ok
    {:result :ok}))

(defn delete-post!
  "Delete a post record by id."
  [id]
  (db/delete-post! id)
  (response/ok
    {:result :ok}))

(comment
  (->> (get-post 1)
       :body)
       
  (s/explain-data
    :blog-post/blog-posts
    (:body (get-posts)))
  
  (s/valid?
    :blog-post/blog-posts
    (:body (get-posts)))

  (->> (get-comments-for 1)
       :body
       first))