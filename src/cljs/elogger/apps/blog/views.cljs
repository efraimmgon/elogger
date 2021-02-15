(ns elogger.apps.blog.views
  (:require
    elogger.apps.blog.handlers
    [elogger.apps.blog.views.blog-post :as b1]
    [elogger.apps.blog.views.blog-posts :as b2]
    [elogger.utils.views :refer [base-ui navbar footer]]))

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
