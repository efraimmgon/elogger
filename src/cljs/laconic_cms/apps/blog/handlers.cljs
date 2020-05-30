(ns laconic-cms.apps.blog.handlers
  (:require
   [ajax.core :as ajax]
   [laconic-cms.utils.events :refer [query base-interceptors dispatch-n]]
   [re-frame.core :as rf :refer 
    [dispatch reg-event-db reg-event-fx reg-sub]]))

; ------------------------------------------------------------------------------
; Handlers
; ------------------------------------------------------------------------------

(def blog-interceptors
  (conj base-interceptors
        (rf/path :blog)))

; ------------------------------------------------------------------------------
; Server callbacks

;; NOTE: massage tags to the format expected by the server before submitting
(reg-event-fx
 :blog/create-post
 blog-interceptors
 (fn [_ [{:keys [doc handler]}]]
   ;; TODO: validate
   (ajax/POST "/api/blog-posts"
              {:params doc
               :handler #(when handler (handler %))
               :error-handler #(dispatch [:ajax-error %])})
   nil))


(reg-event-fx
 :blog/delete-post
 blog-interceptors
 (fn [_ [{post-id :blog-post/id handler :handler}]]
   (ajax/DELETE (str "/api/blog-posts/" post-id)
                {:handler #(when handler (handler %))
                 :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :blog/edit-post
 blog-interceptors
 (fn [_ [{:keys [doc handler]}]]
   ;; TODO: validate
   (ajax/PUT (str "/api/blog-posts/" (:blog-post/id doc))
             {:params (dissoc doc :blog-post/id)
              :handler #(when handler (handler %))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :blog.post/load
 base-interceptors
 (fn [_ [post-id]]
   (ajax/GET (str "/api/blog-posts/" post-id)
             {:handler #(dispatch [:assoc-in [:blog/post] %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :blog.posts/load
 base-interceptors
 (fn [_ _]
   (ajax/GET "/api/blog-posts"
             {:handler #(dispatch [:assoc-in [:blog/posts] %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :blog.post.comment/create
 base-interceptors
 (fn [_ [{:keys [path doc]}]]
   (let [id (:blog-post/id @doc)]
     (ajax/POST (str "/api/blog-posts/" id "/comments")
                {:params @doc
                                       ;; Clear and close the form.
                 :handler #(dispatch-n [:assoc-in (pop path) nil]
                                       [:blog.post/load id])
                 :error-handler #(dispatch [:ajax-error %])}))
   nil))

(reg-event-fx
  :blog.post.comment/like
  base-interceptors
  (fn [_ [comment-id user-id]]
    (let [id (:blog-post/id @(rf/subscribe [:blog/post]))]
      (ajax/POST (str "/api/comments/" comment-id "/likes")
                 {:params {:users/id user-id}
                  :handler #(dispatch [:blog.post/load id])
                  :error-handler #(dispatch [:ajax-error %])}))
    nil))

(reg-event-fx
  :blog.post.comment/unlike
  base-interceptors
  (fn [_ [comment-id like-id]]
    (let [id (:blog-post/id @(rf/subscribe [:blog/post]))]
      (ajax/DELETE (str "/api/comments/" comment-id "/likes/" like-id)
                   {:handler #(dispatch [:blog.post/load id])
                    :error-handler #(dispatch [:ajax-error %])}))
    nil))

; ------------------------------------------------------------------------------
; Subscriptions
; ------------------------------------------------------------------------------

(reg-sub :blog/post query)

(reg-sub :blog/posts query)

;; All comments.
(reg-sub
  :blog.post/comments
  :<- [:blog/post]
  (fn [blog-post _]
    (:blog-post/comments blog-post)))

;; Publicly visible comments.
;; Comments that are approved, and that are not deleted.
(reg-sub 
  :blog.post.comments/visible
  :<- [:blog.post/comments]
  (fn [comments _]
    (->> comments
         (filter #(and (:threaded-comment/is-approved %)
                       (not (:threaded-comment/is-deleted %)))))))

(reg-sub 
  :blog.post.comments.visible/count
  :<- [:blog.post.comments/visible]
  (fn [comments _]
    (count comments)))
