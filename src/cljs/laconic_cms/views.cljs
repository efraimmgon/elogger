(ns laconic-cms.views
  (:require
    [laconic-cms.apps.admin.views :as admin]
    [laconic-cms.apps.home :as home]
    [laconic-cms.apps.blog.views :as blog]
    [laconic-cms.apps.pages.views :as pages]
    [laconic-cms.apps.users.views :as users]
    [markdown.core :refer [md->html]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.core :as reitit]
    [reitit.coercion.spec :as rss]))

(defn page []
  (when-let [active-page @(rf/subscribe [:common/page])]
    [:div
     [active-page]]))

;;; ---------------------------------------------------------------------------
;;; ROUTER

(def router
  (reitit/router
    [""
     ;;; HOME
     ["/"
      {:name :home
       :view #'home/home-ui}]
     
     ;;; ADMIN
     ["/admin"
      [""
       {:name :admin
        :view #'admin/dashboard-ui}]
      ["/users"
       [""
        {:name :admin.users/list
         :view #'admin/users-ui
         :controllers [{:start #(rf/dispatch [:users/load-users])}]}]
       ["/create"
        {:name :admin.user/create
         :view #'admin/create-user-ui
         :controllers [{:start 
                        ;; clear the user fields
                        #(rf/dispatch [:users/set-user nil])}]}]
       ["/{users/id}"
        {:parameters {:path {:users/id int?}}}
        ["/edit"
         {:name :admin.user/edit
          :view #'admin/edit-user-ui
          :controllers [{:parameters {:path [:users/id]}
                         :start (fn [path]
                                  (rf/dispatch
                                    [:users/load-user
                                     (js/parseInt
                                       (get-in path [:path :users/id]))]))}]}]]]
      
      ["/posts"
       [""
        {:name :admin.posts/list
         :view #'admin/posts-ui
         :controllers [{:start #(rf/dispatch [:blog.posts/load])}]}]
       ["/create"
        {:name :admin.post/create
         :view #'admin/create-post-ui
         :controllers [{:start
                        ;; clear the blog-post fields 
                        #(rf/dispatch [:assoc-in [:blog/post] nil])}]}]
       ["/{blog-post/id}"
        {:parameters {:path {:blog-post/id int?}}}
        ["/edit"
         {:name :admin.post/edit
          :view #' admin/edit-post-ui
          :controllers [{:parameters {:path [:blog-post/id]}
                         :start (fn [path]
                                  (rf/dispatch
                                    [:blog.post/load
                                     (js/parseInt
                                       (get-in path [:path :blog-post/id]))]))}]}]]]
      
      ["/pages"
       [""
        {:name :admin.pages/list
         :view #'admin/pages-ui
         :controllers [{:start #(rf/dispatch [:pages/load])}]}]
       ["/create"
        {:name :admin.page/create
         :view #'admin/create-page-ui
         :controllers [{:start
                        ;; clear the page fields
                        #(rf/dispatch [:assoc-in [:pages/page] nil])}]}]
       ["/{page/id}"
        {:parameters {:path {:page/id int?}}}
        ["/edit"
         {:name :admin.page/edit
          :view #'admin/edit-page-ui
          :controllers [{:parameters {:path [:page/id]}
                         :start (fn [path]
                                  (rf/dispatch
                                    [:pages/load-page
                                     (js/parseInt
                                       (get-in path [:path :page/id]))]))}]}]]]
      ["/comments"
       [""
        {:name :admin.comments/list
         :view #'admin/comments-ui
         :controllers [{:start #(rf/dispatch [:blog-posts.comments/load-comments])}]}]
       ["/{threaded-comment/id}"
        {:parameters {:path {:threaded-comment/id int?}}}
        ["/edit"
         {:name :admin.comment/edit
          :view #'admin/edit-comment-ui
          :controllers [{:parameters {:path [:threaded-comment/id]}
                         :start (fn [path]
                                  (rf/dispatch
                                    [:comments/load-comment
                                     (js/parseInt
                                       (get-in path [:path :threaded-comment/id]))]))}]}]]]]
     
     ;;; BLOG
     ["/blog"
      [""
       {:name :blog/list
        :view #'blog/blog-posts-ui
        :controllers [{:start (fn [_]
                                (rf/dispatch
                                  [:blog.posts/load]))}]}]
      ["/{blog-post/id}"
       {:parameters {:path {:blog-post/id int?}}}
       [""
        {:name :blog/view
         :view #'blog/blog-post-ui
         :controllers [{:parameters {:path [:blog-post/id]}
                        :start (fn [params]
                                 (rf/dispatch
                                   [:blog.post/load
                                    (js/parseInt 
                                      (get-in params [:path :blog-post/id]))]))}]}]]]

     ;;; USERS
     ["/users"
      ["/{users/id}"
       {:parameters {:path {:users/id int?}}}
       [""
        {:name :profile/view
         :view #'users/profile-ui
         :controllers [{:parameters {:path [:users/id]}
                        :start (fn [params]
                                 (rf/dispatch
                                   [:users/load-profile-user
                                    (js/parseInt (get-in params [:path :users/id]))]))}]}]
       ["/edit"
        {:name :profile/edit
         :view #'users/edit-profile-ui
         :controllers [{:parameters {:path [:users/id]}
                        :start (fn [params]
                                 (rf/dispatch
                                   [:users/load-profile-user
                                    (js/parseInt (get-in params [:path :users/id]))]))}]}]]]

     ;;; PAGES
     ["/p/{page/id}"
      {:name :page/view
       :parameters {:path {:page/id int?}}
       :view #'pages/page-ui
       :controllers [{:parameters {:path [:page/id]}
                      :start (fn [params]
                               (rf/dispatch
                                 [:pages/load-page
                                  (js/parseInt (get-in params [:path :page/id]))]))}]}]]
       
    {:coercion rss/coercion}))
