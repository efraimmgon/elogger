(ns elogger.routes.services
  (:require
    [buddy.auth :refer [authenticated?]]
    [clojure.spec.alpha :as s]
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [elogger.middleware.formats :as formats]
    [elogger.middleware.exception :as exception]

    [elogger.routes.services.auth :as auth]
    [elogger.routes.services.blog-post :as blog-post]
    [elogger.routes.services.threaded-comment :as comment]
    [elogger.routes.services.like :as like]
    [elogger.routes.services.page :as page]
    [elogger.routes.services.user :as user]
    [elogger.routes.services.settings :as settings]
    [elogger.utils :refer [string->date]]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]))

(defn admin? [{:keys [identity] :as req}]
  (and identity
       (:users/admin identity)))

(defn forbidden-error []
  (forbidden
    {:error "Action not permitted for this user."}))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}
    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]


   ; -----------------------------------------------------------------------
   ;; Auth

   ["/login"
    {:post {:summary "Create a session for the user, logging him in."
            :parameters {:header (s/keys :req-un [:auth/authorization])}
            :responses {200 {:body :users/User}}
            :handler (fn [{:keys [parameters] :as req}]
                       (auth/login!
                         req
                         (get-in parameters [:header :authorization])))}}]
   ["/logout"
    {:post {:summary "Remove the user session."
            :responses {200 {:body :result/Result}}
            :handler (fn [req]
                       (auth/logout!))}}]
   ["/register"
    {:post {:summary "Create a new user and profile records, loggin the user in"
            :parameters {:body :auth/UserRegistration}
            :responses {200 {:body :users/User}}
            :handler (fn [{:keys [parameters] :as req}]
                       (auth/register! req (:body parameters)))}}]
   ["/authenticated"
    {:get {:summary "Is the user logged in?"
           :handler (fn [req]
                      (ok {:user (:identity req)}))}}]
   
   ["/checkin"
    {:post {:summary "Checks the user into his office hours."
            :parameters {:body (s/keys :req [:office-hours/user-id
                                             :office-hours/lat
                                             :office-hours/lng])}
            :responses {200 {:body :result/Result}}
            :handler (fn [{:keys [parameters] :as req}]
                       (if (user/any-granted? 
                             (:identity req)
                             (get-in parameters [:body :office-hours/user-id]))
                         (auth/checkin!
                           (assoc (:body parameters) :office-hours/user-agent
                             (get-in req [:headers "user-agent"])))
                         (forbidden-error)))}}]
   ["/checkout"
    {:post {:summary "Checks the user out of his office hours."
            :parameters {:body (s/keys :req [:office-hours/user-id
                                             :office-hours/lat
                                             :office-hours/lng])}
            :responses {200 {:body :result/Result}}
            :handler (fn [{:keys [parameters] :as req}]
                       (if (user/any-granted? 
                             (:identity req)
                             (get-in parameters [:body :office-hours/user-id]))
                         (auth/checkout!
                           (assoc (:body parameters) :office-hours/user-agent
                             (get-in req [:headers "user-agent"])))
                         (forbidden-error)))}}]
   ["/admin/settings"
    {:get {:summary "Returns the site's admin settings for the frontend."
           :responses {200 {:body :admin/Settings}}
           :handler (fn [req]
                      (if (admin? req)
                        (settings/get-admin-settings)
                        (forbidden-error)))}
     :put {:summary "Updates the site's admin settings."
           :parameters {:body :admin/Settings}
           :responses {200 {:body :result/Result}}
           :handler (fn [{:keys [parameters] :as req}]
                      (if (admin? req)
                        (settings/update-admin-settings
                          (:body parameters))
                        (forbidden-error)))}}]
                      
                        
   
                         
                            

   ;;; -----------------------------------------------------------------------
   ;;; Users

   ["/users"
    [""
     {:get {:summary "Return all user records."
            :responses {200 {:body :users/users}}
            :handler (fn [req]
                       (if (admin? req)
                         (user/get-users)
                         (forbidden-error)))}
      :post {:summary "Create a user record in the db."
             :parameters {:body (s/keys :req [:users/username
                                              :users/password
                                              :users/admin
                                              :users/is-active]
                                        :opt [:profile/first-name
                                              :profile/last-name
                                              :users/email])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters]}]
                        (user/create-user-with-profile!
                          (:body parameters)))}}]
    ["/office-hours/last-checkin"
     {:get {:summary "Return all the users last office hours checkin row, 
                     along with the users info."
            :responses {200 {:body :users/users}}
            :handler (fn [req]
                       (if (admin? req)
                         (user/get-users-office-hours-last-checkin)
                         (forbidden-error)))}}]
    ["/{users/id}"
     {:parameters {:path (s/keys :req [:users/id])}}
     [""
      {:get {:summary "Return a user record by id."
             :responses {200 {:body :users/User}}
             :handler (fn [{:keys [parameters]}]
                        (user/get-user
                          (get-in parameters [:path :users/id])))}
       :put {:summary "Update a user record with params."
             :parameters {:body (s/keys :req  [:users/admin
                                               :users/email
                                               :users/is-active]
                                        :opt  [:users/profile])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (user/any-granted?
                              (:identity req)
                              (get-in parameters [:path :users/id]))
                          (user/update-user-with-profile!
                            (merge (:path parameters)
                                   (:body parameters)))
                          (forbidden-error)))}
       :delete {:summary "Delete a user record by id."
                :responses {200 {:body :result/Result}}
                :handler (fn [{:keys [parameters] :as req}]
                           (let [user-id (get-in parameters [:path :users/id])]
                               (if (user/any-granted?
                                     (:identity req)
                                     user-id)
                                 (user/delete-user! user-id)
                                 (forbidden-error))))}}]
     ["/office-hours"
      {:get {:summary "Get all the user's info and office-hours rows for this user."
             :responses {200 {:body :users/User}}
             :handler (fn [{:keys [parameters] :as req}]
                        (let [user-id (get-in parameters [:path :users/id])]
                          (if (user/any-granted?
                                (:identity req)
                                user-id)
                            (user/get-user-office-hours-by-user-id user-id)
                            (forbidden-error))))}}]]]
      
   ;; -----------------------------------------------------------------------
   ;; BLOG POSTS

   ["/blog-posts"
    [""
     {:get {:summary "Get all blog post records."
            :responses {200 {:body :blog-post/blog-posts}}
            :handler (fn [_]
                       (blog-post/get-posts))}
      :post {:summary "Create a blog post record."
             :parameters {:body (s/keys :req [:blog-post/title
                                              :blog-post/status
                                              :blog-post/content
                                              :blog-post/author-id]
                                        :opt [:blog-post/subtitle
                                              :blog-post/featured-img-id
                                              :blog-post/published-at
                                              :blog-post/expires-at])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (authenticated? req)
                          (blog-post/create-post!
                            (:body parameters))
                          (forbidden
                            {:error-msg "Action not permited for this user."})))}}]
    ["/{blog-post/id}"
     {:parameters {:path (s/keys :req [:blog-post/id])}}
     [""
      {:get {:summary "Get a blog post record by id."
             :responses {200 {:body :blog-post/BlogPost}}
             :handler (fn [{:keys [parameters] :as req}]
                        (blog-post/get-post
                          (get-in parameters [:path :blog-post/id])))}
       :put {:summary "Update a blog post record by id."
             :parameters {:body (s/keys :req [:blog-post/title
                                              :blog-post/status
                                              :blog-post/content
                                              :blog-post/author-id]
                                        :opt [:blog-post/subtitle
                                              :blog-post/featured-img-id
                                              :blog-post/published-at
                                              :blog-post/expires-at])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (blog-post/any-granted?
                              (:identity req)
                              (get-in parameters [:path :blog-post/id]))
                          (blog-post/update-post!
                            (merge (:path parameters)
                                   (:body parameters)))
                          (forbidden
                            {:error-msg "Action not permited for this user."})))}
       :delete {:summary "Delete a blog post record by id."
                :responses {200 {:body :result/Result}}
                :handler (fn [{:keys [parameters] :as req}]
                            (if (blog-post/any-granted?
                                  (:identity req)
                                  (get-in parameters [:path :blog-post/id]))
                              (blog-post/delete-post!
                                (get-in parameters [:path :blog-post/id]))
                              (forbidden
                                {:error-msg "Action not permited for this user."})))}}]
     ["/comments"
      {:get {:summary "Get all comment records for a blog post."
             :responses {200 {:body :blog-post/comments-for-blog-post}}
             :handler (fn [{:keys [parameters] :as req}]
                        (blog-post/get-comments-for
                          (get-in parameters [:path :blog-post/id])))}
       :post {:summary "Create a comment for a blog post by id."
              :parameters {:body (s/keys :req [:threaded-comment/title
                                               :threaded-comment/body]
                                         :opt [:threaded-comment/is-approved
                                               :threaded-comment/parent
                                               :threaded-comment/author-id
                                               :threaded-comment/author-name])}
              :responses {200 {:body :result/Result}}
              :handler (fn [{:keys [parameters] :as req}]
                         (if (authenticated? req)
                           (blog-post/create-comment!
                             (merge (:path parameters)
                                    (:body parameters)))
                           (forbidden
                            {:error-msg "You must have an account to comment."})))}}]]   

    ["/comments/all"
     {:get {:summary "Get all comment records for all blog posts."
            :responses {200 {:body :blog-post/all-comments}}
            :handler (fn [_]
                      (blog-post/get-all-comments))}}]]

   ;;; -----------------------------------------------------------------------
   ;;; PAGES

   ["/pages"
    [""
     {:get {:summary "Get all page records."
            :responses {200 {:body :page/pages}}
            :handler (fn [_]
                       (page/get-pages))}
      :post {:summary "Create a page record."
             :parameters {:body (s/keys :req [:page/title
                                              :page/status
                                              :page/content
                                              :page/author-id]
                                        :opt [:page/subtitle
                                              :page/featured-img-id
                                              :page/published-at
                                              :page/expires-at])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (authenticated? req)
                          (page/create-page! (:body parameters))
                          (forbidden
                           {:error-msg "Action not permited for this user."})))}}]
    ["/{page/id}"
     {:parameters {:path (s/keys :req [:page/id])}}
     [""
      {:get {:summary "Get a page record by id."
             :responses {200 {:body :page/Page}}
             :handler (fn [{:keys [parameters] :as req}]
                        (page/get-page (get-in parameters [:path :page/id])))}
       :put {:summary "Update a page record by id."
             :parameters {:body (s/keys :req [:page/title
                                              :page/status
                                              :page/content
                                              :page/author-id]
                                        :opt [:page/subtitle
                                              :page/featured-img-id
                                              :page/published-at])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (page/any-granted?
                              (:identity req)
                              (get-in parameters [:path :page/id]))
                          (page/update-page!
                            (merge (:path parameters)
                                   (:body parameters)))
                          (forbidden
                           {:error-msg "Action not permited for this user."})))}
       :delete {:summary "Delete a page record by id."
                :responses {200 {:body :result/Result}}
                :handler (fn [{:keys [parameters] :as req}]
                           (let [page-id (get-in parameters [:path :page/id])]
                             (if (page/any-granted?
                                   (:identity req)
                                   page-id)
                               (page/delete-page! page-id)
                               (forbidden
                                {:error-msg "Action not permited for this user."}))))}}]]]

   ;;; -----------------------------------------------------------------------
   ;;; COMMENTS

   ["/comments"
    [""
     {:get {:summary "Get all comment records."
            ; :return (s/map-of keyword?
            ;                  (s/nilable coll?))
            :handler (fn [_]
                       (comment/get-comments))}
      :post {:summary "Create a comment record."
             :parameters {:body (s/keys :req [:threaded-comment/title
                                              :threaded-comment/body]
                                        :opt [:threaded-comment/is-approved
                                              :threaded-comment/author-id
                                              :threaded-comment/parent
                                              :threaded-comment/author-name])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (authenticated? req)
                          (comment/create-comment!
                            (:body parameters))
                          (forbidden
                           {:error-msg "Action not permited for this user."})))}}]
    ["/{threaded-comment/id}"
     {:parameters {:path (s/keys :req [:threaded-comment/id])}}
     [""
      {:get {:summary "Get a comment record by id."
             :responses {200 {:body :threaded-comment/Comment}}
             :handler (fn [{:keys [parameters] :as req}]
                        (comment/get-comment
                          (get-in parameters [:path :threaded-comment/id])))}
       ; NOTE: users can create, delete and fetch blog post comments, but
       ; they cannot edit them.
       ; Why is that? It seems a better way to make people honest, without
       ; preventing them from retracting their opinions, since
       ; they can still delete their comments (but only if they were
       ; registered).
       ; Perhaps the best solution would be to allow the users to edit their
       ; comments, but we would keep a record of the previous comment for
       ; future reference.
       :put {:summary "Update a comment record by id."
             :parameters {:body (s/keys :req [:threaded-comment/body]
                                        :opt [:threaded-comment/is-approved])}
             :responses {200 {:body :result/Result}}
             :handler (fn [{:keys [parameters] :as req}]
                        (if (admin? req)
                          (comment/update-comment!
                            (merge (:path parameters)
                                   (:body parameters)))
                          (forbidden
                           {:error-msg "Action not permited for this user."})))}
       :delete {:summary "Delete a comment record by id."
                :responses {200 {:body :result/Result}}
                :handler (fn [{:keys [parameters] :as req}]
                           (let [comment-id (get-in parameters [:path :threaded-comment/id])]
                             (if (comment/any-granted?
                                   (:identity req)
                                   comment-id)
                               (comment/delete-comment! comment-id)
                               (forbidden
                                {:error-msg "Action not permited for this user."}))))}}]
     ["/likes"
      {:post {:summary "Create a like record for a comment by id."
              :parameters {:path (s/keys :req [:threaded-comment/id])
                           :body (s/keys :req [:users/id])}
              :responses {200 {:body :result/Result}}
              :handler (fn [{:keys [parameters] :as req}]
                         (if (authenticated? req)
                           (comment/like!
                             (merge (:path parameters)
                                    (:body parameters)))
                           (forbidden
                            {:error-msg "Action not permited for this user."})))}}]]]

   ["/likes/{likes/id}"
    {:delete {:summary "Delete a like record for a comment, by ids."
              :parameters {:path (s/keys :req [:likes/id])}
              :responses {200 {:body :result/Result}}
              :handler (fn [{:keys [parameters] :as req}]
                         (let [like-id (get-in parameters [:path :likes/id])]
                           (if (like/any-granted?
                                 (:identity req)
                                 like-id)
                             (like/delete! like-id)
                             (forbidden
                              {:error-msg "Action not permited for this user."}))))}}]




   ["/math"
    {:swagger {:tags ["math"]}}

    ["/plus"
     {:get {:summary "plus with spec query parameters"
            :parameters {:query {:x int?, :y int?}}
            :responses {200 {:body {:total pos-int?}}}
            :handler (fn [{{{:keys [x y]} :query} :parameters :as req}]
                       (clojure.pprint/pprint req)
                       {:status 200
                        :body {:total (+ x y)}})}
      :post {:summary "plus with spec body parameters"
             :parameters {:body {:x int?, :y int?}}
             :responses {200 {:body {:total pos-int?}}}
             :handler (fn [{{{:keys [x y]} :body} :parameters :as req}]
                        (clojure.pprint/pprint req)
                        {:status 200
                         :body {:total (+ x y)}})}}]]

   ["/files"
    {:swagger {:tags ["files"]}}

    ["/upload"
     {:post {:summary "upload a file"
             :parameters {:multipart {:file multipart/temp-file-part}}
             :responses {200 {:body {:name string?, :size int?}}}
             :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                        {:status 200
                         :body {:name (:filename file)
                                :size (:size file)}})}}]

    ["/download"
     {:get {:summary "downloads a file"
            :swagger {:produces ["image/png"]}
            :handler (fn [_]
                       {:status 200
                        :headers {"Content-Type" "image/png"}
                        :body (-> "public/img/warning_clojure.png"
                                  (io/resource)
                                  (io/input-stream))})}}]]])
