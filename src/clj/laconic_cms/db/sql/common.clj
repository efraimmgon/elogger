(ns laconic-cms.db.sql.common)

;;; ---------------------------------------------------------------------------
;;; Joins


; -----------------------------------------------------------------------------
; Columns

(def user-columns
  [:users/id, 
   :users/username,
   :users/email, 
   :users/admin, 
   :users/last-login, 
   :users/is-active,
   :users/created-at, 
   :users/updated-at, 
   :users/password])

(def profile-columns
  [:profile/id, 
   :profile/user-id, 
   :profile/first-name
   :profile/last-name, 
   :profile/bio, 
   :profile/profile-picture-id
   :profile/created-at, 
   :profile/updated-at])

(def page-columns
  [:page/id, 
   :page/title, 
   :page/subtitle, 
   :page/status, 
   :page/content,
   :page/featured-img-id, 
   :page/author-id, 
   :page/created-at, 
   :page/updated-at,
   :page/published-at, 
   :page/expires-at])

(def blog-post-columns
  [:blog-post/id, 
   :blog-post/title, 
   :blog-post/subtitle,
   :blog-post/status, 
   :blog-post/content, 
   :blog-post/author-id,
   :blog-post/featured-img-id, 
   :blog-post/created-at, 
   :blog-post/updated-at,
   :blog-post/published-at, 
   :blog-post/expires-at])

(def threaded-comment-columns
  [:threaded-comment/id 
   :threaded-comment/author-id
   :threaded-comment/author-name 
   :threaded-comment/body
   :threaded-comment/title
   :threaded-comment/parent 
   :threaded-comment/is-approved
   :threaded-comment/is-deleted 
   :threaded-comment/created-at
   :threaded-comment/updated-at])

(def like-columns
  [:likes/id :likes/user-id :likes/created-at :likes/updated-at])

; -----------------------------------------------------------------------------
; Queries

(def user-query
  (conj user-columns
        {:users/profile profile-columns}))

(def like-query
  (conj like-columns
        {:likes/author user-query}))


(defn author-query [k]
  {k user-query})

(def comment-query
  (conj threaded-comment-columns
        {:threaded-comment/likes like-query}
        {:threaded-comment/author user-query}))

(def threaded-comment-query comment-query)

(def blog-post-query
  (conj blog-post-columns
        {:blog-post/comments comment-query}
        {:blog-post/likes like-query}
        {:blog-post/author user-query}))

(def blog-post-comment-query
  [:blog-post/id {:blog-post/comments comment-query}])

(def page-query
  (conj page-columns
        {:page/author user-query}))
