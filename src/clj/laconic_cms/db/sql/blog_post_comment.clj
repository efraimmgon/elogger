(ns laconic-cms.db.sql.blog-post-comment
  (:require
    [laconic-cms.db.core :as db]))

(defn create! 
  "Create a blog-post comment relation."
  [params]
  (db/insert! "blog_post_comment" {:blog-post-id (:blog-post/id params)
                                   :comment-id (:threaded-comment/id params)}))
