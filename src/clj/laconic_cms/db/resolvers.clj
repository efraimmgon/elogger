(ns laconic-cms.db.resolvers
  (:require
    [com.wsscode.pathom.connect :as pc :refer
     [defresolver]]
    [laconic-cms.db.core :as db]
    [laconic-cms.db.sql.common :as common]))

;;; ---------------------------------------------------------------------------
;;; USERS

;;; HELPERS

(defn get-user
  "Retrives a user record given the user-id."
  [user-id]
  (db/execute-one!
    ["SELECT * FROM users u WHERE u.id = ?" user-id]))
        

(defn get-users
  "Retrieves all user records."
  []
  (db/execute!
    ["SELECT * FROM users u"]))
    
    
(defn get-user-by-username
  "Retrieves a user record given the username."
  [username]
  (db/execute-one!
    ["SELECT * FROM users u WHERE u.username = ?" username]))

(defn get-profile-by-id [id]
  (db/execute-one!
    ["SELECT * FROM profile p WHERE p.id = ?" id]))

(defn get-profile-by-user-id [id]
  (db/execute-one!
    ["SELECT * FROM profile p WHERE p.user_id = ?" id]))

(defn get-profiles []
  (db/execute!
    ["SELECT * FROM profile p"]))

;;; RESOLVERS

(defresolver users [env input]
  {::pc/output [{:users/list common/user-columns}]}
  (some->>
    (get-users)
    seq
    (hash-map :users/list)))

(defresolver user-by-id [env input]
  {::pc/input #{:users/id}
   ::pc/output common/user-columns}
  (get-user (:users/id input)))

(defresolver user-by-username [env input]
  {::pc/input #{:users/username}
   ::pc/output common/user-columns}
  (get-user-by-username (:users/username input)))

(defresolver user->profile [env {:keys [users/id]}]
  {::pc/input #{:users/id}
   ::pc/output [{:users/profile [:profile/id]}]}
  (let [ret (-> (get-profile-by-user-id id) (select-keys [:profile/id]))]
    (when (seq ret)
      {:users/profile ret})))

(defresolver profile-by-id [env {:keys [profile/id]}]
  {::pc/input #{:profile/id}
   ::pc/output common/profile-columns}
  (get-profile-by-id id))

(defresolver profile-by-user-id [env {:keys [users/id]}]
  {::pc/input #{:users/id}
   ::pc/output common/profile-columns}
  (get-profile-by-user-id id))

(defresolver profiles [env input]
  {::pc/output [{:profiles/list common/profile-columns}]}
  (some->>
    (get-profiles)
    seq
    (hash-map :profiles/list)))

(def user-registry
  [users
   user-by-id 
   user-by-username
   user->profile
   profile-by-id
   profile-by-user-id 
   profiles])

;;; ---------------------------------------------------------------------------
;;; PAGE

;;; HELPERS

(defn get-page-by-id
  "Retrieves a page record given the id."
  [id]
  (db/execute-one!
    ["SELECT * FROM page WHERE page.id = ?" id])) 

(defn get-pages
  "Retrieves all page records."
  []
  (db/execute!
    ["SELECT * FROM page "]))

;;; RESOLVERS

(defresolver page-by-id [env {:keys [page/id]}]
  {::pc/input #{:page/id}
   ::pc/output common/page-columns}
  (get-page-by-id id))

(defresolver pages [_ _]
  {::pc/output [{:pages/list common/page-columns}]}
  (some->>
    (get-pages)
    seq
    (hash-map :pages/list)))

(defresolver page->author [env {:keys [page/id]}]
  {::pc/input #{:page/id}
   ::pc/output [{:page/author [:users/id]}]}
  (some->>
    (get-page-by-id id)
    :page/author-id (hash-map :users/id)
    (hash-map :page/author)))
      
(def page-registry
  [page-by-id
   pages
   page->author])

;;; ---------------------------------------------------------------------------
;;; LIKE

;;; HELPERS

(defn get-by-id [id]
  (db/execute-one!
    ["SELECT * FROM likes WHERE id = ?" id]))

(defn get-all []
  (db/execute! ["SELECT * FROM likes"]))

;;; RESOLVERS

(defresolver like-by-id [env {:keys [likes/id]}]
  {::pc/input #{:likes/id}
   ::pc/output common/like-columns}
  (get-by-id id))

(defresolver likes [_ _]
  {::pc/output [{:likes/list common/like-columns}]}
  (some->> 
    (get-all)
    seq
    (hash-map :likes/list)))

(defresolver like->author [env {:keys [likes/id]}]
  {::pc/input #{:likes/id}
   ::pc/output [{:likes/author [:users/id]}]}
  (some->>
    (get-by-id id)
    :likes/user-id (hash-map :users/id)
    (hash-map :likes/author)))

(def like-registry
  [like-by-id
   likes
   like->author])

;;; ---------------------------------------------------------------------------
;;; THREADED-COMMENT

;;; HELPERS

(defn get-comments
  "Return all comment records."
  []
  (db/execute! ["SELECT * FROM threaded_comment tc"])) 

(defn get-comment-by-id
  "Return a comment record by id."
  [id]
  (db/execute-one! 
    ["SELECT * FROM threaded_comment tc WHERE tc.id = ?" id]))
                     
          
(defn get-thread
  "Retrieves a comment thread, including the root comment."
  [parent-id]
  (db/execute!
    ["SELECT * FROM threaded_comment tc 
      WHERE tc.parent = ? OR tc.id = ?" parent-id parent-id]))
          

;;; RESOLVERS

(defresolver all-comments [_ _]
  {::pc/output [{:threaded-comments/list common/threaded-comment-columns}]}
  (some->>
    (get-comments)
    seq
    (hash-map :threaded-comments/list)))

(defresolver comment-by-id [env {:keys [threaded-comment/id]}]
  {::pc/input #{:threaded-comment/id}
   ::pc/output common/threaded-comment-columns}
  (get-comment-by-id id))

(defresolver comment-thread [env {:keys [threaded-comment/parent]}]
  {::pc/input #{:threaded-comment/parent}
   ::pc/output [{:threaded-comment/thread common/threaded-comment-columns}]}
  (some->>
    (get-thread parent)
    seq
    (hash-map :threaded-comment/thread)))

(defresolver comment->likes [env {:keys [threaded-comment/id] :as input}]
  {::pc/input #{:threaded-comment/id}
   ::pc/output [{:threaded-comment/likes [:likes/id]}]}
  (some->>
    (db/execute! ["SELECT * FROM comment_likes cl
                  WHERE cl.comment_id = ?" id])
    seq
    (mapv (fn [row]
            {:likes/id (:comment-likes/like-id row)}))
    (hash-map :threaded-comment/likes)))
   

(defresolver comment->author [env {:keys [threaded-comment/id]}]
  {::pc/input #{:threaded-comment/id}
   ::pc/output [{:threaded-comment/author [:users/id]}]}
  (some->>
    (get-comment-by-id id)
    :threaded-comment/author-id (hash-map :users/id)
    (hash-map :threaded-comment/author)))

(def threaded-comment-registry
  [all-comments
   comment-by-id
   comment-thread
   comment->likes
   comment->author])

;;; ---------------------------------------------------------------------------
;;; BLOG-POST

;;; HELPERS

(defn get-post-by-id
  "Retrieves a blog-post record given the id."
  [id]
  (db/execute-one!
    ["SELECT * FROM blog_post bp WHERE bp.id = ?" id])) 

(defn get-posts
  "Retrieves all blog-post records."
  []
  (db/execute!
    ["SELECT * FROM blog_post bp"]))

;;; RESOLVERS

(defresolver post-by-id [env {:keys [blog-post/id]}]
  {::pc/input #{:blog-post/id}
   ::pc/output common/blog-post-columns}
  (get-post-by-id id))

(defresolver all-posts [_ _]
  {::pc/output [{:blog-posts/list common/blog-post-columns}]}
  (some->>
    (get-posts)
    seq
    (hash-map :blog-posts/list)))

(defresolver post->comments [env {:keys [blog-post/id]}]
  {::pc/input #{:blog-post/id}
   ::pc/output [{:blog-post/comments [:threaded-comment/id]}]}
  (some->>
    (db/execute! ["SELECT * FROM blog_post_comment bpc
                  WHERE bpc.blog_post_id = ?" id])
    seq
    (mapv #(->> % :blog-post-comment/comment-id (hash-map :threaded-comment/id)))
    (hash-map :blog-post/comments)))

(defresolver post->author [env {:keys [blog-post/id]}]
  {::pc/input #{:blog-post/id}
   ::pc/output [{:blog-post/author [:users/id]}]}
  (some->> (get-post-by-id id)
           :blog-post/author-id (hash-map :users/id)
           (hash-map :blog-post/author)))

(def blog-post-registry
  [post-by-id
   all-posts
   post->comments
   post->author])
