(ns laconic-cms.apps.blog.views
  (:require
    laconic-cms.apps.blog.handlers
    [laconic-cms.apps.blog.views.blog-post :as b1]
    [laconic-cms.apps.blog.views.blog-posts :as b2]
    [laconic-cms.utils.views :refer [base-ui navbar footer]]))

(defn blog-base-ui [body]
  [base-ui
   [:div.blog
    [navbar]
    body
    [footer]]])

(defn blog-post-ui []
  [blog-base-ui [b1/blog-post-ui]])

(defn blog-posts-ui []
  [blog-base-ui [b2/blog-posts-ui]])
