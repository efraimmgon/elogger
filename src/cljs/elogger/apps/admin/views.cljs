(ns elogger.apps.admin.views
  (:require
    elogger.apps.admin.handlers
    [elogger.apps.admin.utils :refer [admin-page-ui]]
    [elogger.apps.admin.dashboard :as dashboard]
    [elogger.apps.admin.blog :as blog]
    [elogger.apps.admin.comments :as comments]
    [elogger.apps.admin.pages :as pages]
    [elogger.apps.admin.users :as users]
    [elogger.apps.admin.office-hours :as office-hours]
    [elogger.utils.deps :refer [with-deps]]
    [elogger.utils.events :refer [<sub]]
    [elogger.utils.views :refer [modal-ui error-modal-ui page-ui]]
    elogger.apps.admin.settings
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))


;;; Dashboard

(defmethod page-ui :admin [_]
  [admin-page-ui "Dashboard" [dashboard/dashboard-ui]])

;;; Pages

(defn pages-ui []
  [admin-page-ui "Pages" [pages/pages-panel-ui]])

(defn create-page-ui []
  [admin-page-ui "Create page" [pages/create-page-panel-ui]])

(defn edit-page-ui []
  [admin-page-ui "Edit page" [pages/edit-page-panel-ui]])

;;; Users

(defn users-ui []
  [admin-page-ui "Users" [users/users-panel-ui]])

(defn create-user-ui []
  [admin-page-ui "Create user" [users/create-user-panel-ui]])

(defn edit-user-ui []
  [admin-page-ui "Edit user" [users/edit-user-panel-ui]])

;;; Office hours

(defn office-hours-ui []
  [admin-page-ui "Jornada de trabalho" [office-hours/office-hours-panel-ui]])

(defn view-user-office-hours-ui []
  [admin-page-ui "Jornadas de trabalho" 
   [office-hours/view-user-office-hours-panel-ui]])

;;; Posts

(defn posts-ui []
  [admin-page-ui "Blog posts" [blog/posts-panel-ui]])

(defn create-post-ui []
  [admin-page-ui "Create post" [blog/create-post-panel-ui]])

(defn edit-post-ui []
  [admin-page-ui "Edit post" [blog/edit-post-panel-ui]])

;;; Comments

(defn comments-ui []
  [admin-page-ui "Comments" [comments/comments-ui]])

(defn edit-comment-ui []
  [admin-page-ui "Edit comment" [comments/edit-comment-ui]])
