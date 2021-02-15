(ns elogger.apps.comments.handlers
  (:require
   [ajax.core :as ajax]
   [elogger.utils.events :refer [query base-interceptors]]
   [re-frame.core :as rf :refer 
    [dispatch reg-event-db reg-event-fx reg-sub]]))

(reg-event-fx
 :blog-posts.comments/load-comments
 base-interceptors
 (fn [_ _]
   (ajax/GET "/api/blog-posts/comments/all"
             {:handler #(dispatch [:assoc-in [:comments/list] %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :comments/load-comment
 base-interceptors
 (fn [_ [comment-id]]
   (ajax/GET (str "/api/comments/" comment-id)
             {:handler #(dispatch [:assoc-in [:comments/comment] %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :comments/update-comment
 base-interceptors
 (fn [_ [{:keys [doc handler]}]]
   (ajax/PUT (str "/api/comments/" (:threaded-comment/id doc))
             {:params (dissoc doc :threaded-comment/id)
              :handler (when handler #(handler %))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
  :comments/delete-comment
  base-interceptors
  (fn [_ [{comment-id :threaded-comment/id handler :handler}]]
    (ajax/DELETE (str "/api/comments/" comment-id)
                 {:handler (when handler #(handler %))
                  :error-handler #(dispatch [:ajax-error %])})
    nil))

; :user-id and :comment-id
(reg-event-fx
  :comment/like
  base-interceptors
  (fn [_ [comment-id user-id]]
    (ajax/POST (str "/api/comments/" comment-id "/likes")
               {:params {:users/user-id user-id}
                :error-handler #(dispatch [:ajax-error %])})
    nil))
                
(reg-event-fx
  :comment/unlike
  base-interceptors
  (fn [_ [comment-id like-id]]
    (ajax/DELETE (str "/api/comments/" comment-id "/likes/" like-id)
                 {:error-handler #(dispatch [:ajax-error %])})
    nil))

(rf/reg-sub :comments/list query)
(rf/reg-sub :comments/comment query)