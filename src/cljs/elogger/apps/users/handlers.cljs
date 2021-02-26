(ns elogger.apps.users.handlers
  (:require
    [ajax.core :as ajax]
    [elogger.utils.events :refer 
     [query base-interceptors dispatch-n]]
    [re-frame.core :refer [dispatch reg-event-fx reg-event-db reg-sub]]))

;;; ---------------------------------------------------------------------------
;;; Event handlers

(defn date-coercer [d]
  (first
    (clojure.string/split d #"\.")))


(reg-event-fx
 :users/create-user
 base-interceptors
 (fn [_ [{:keys [doc handler]}]]
   (ajax/POST "/api/users"
              {:params doc
               :handler #(when handler (handler %))
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :users/delete-user
 base-interceptors
 (fn [_ [{user-id :users/id handler :handler}]]
   (ajax/DELETE (str "/api/users/" user-id)
                {:handler #(when handler (handler %))
                 :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :users/edit-user
 base-interceptors
 (fn [_ [{:keys [doc handler]}]]
   (ajax/PUT (str "/api/users/" (:users/id doc))
             {:params (dissoc doc :users/id)
              :handler #(when handler (handler %))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :users/update-profile
 base-interceptors
 (fn [_ [user]]
   (let [{:keys [users/id]} @user]
     (ajax/PUT (str "/api/users/" id)
               {:params (dissoc @user :users/id)
                :handler #(dispatch [:navigate! :profile/view {:users/id id}])
                :error-handler #(dispatch [:ajax-error %])}))
   nil))

(reg-event-fx
 :users/load-user
 base-interceptors
 (fn [_ [user-id]]
   (ajax/GET (str "/api/users/" user-id)
             {:handler #(dispatch [:users/set-user %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
  :users.office-hours/last-checkin
  base-interceptors
  (fn [_ _]
    (ajax/GET "/api/users/office-hours/last-checkin"
              {:handler #(dispatch [:users.office-hours.last-checkin/set %])
               :error-handler #(dispatch [:ajax-error %])
               :response-format :json
               :keywords? true})
    nil))


(reg-event-fx
 :users/load-profile-user
 base-interceptors
 (fn [_ [user-id]]
   (ajax/GET (str "/api/users/" user-id)
             {:handler #(dispatch [:assoc-in [:users/profile] %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :users/load-users
 base-interceptors
 (fn [_ _]
   (ajax/GET "/api/users"
             {:handler #(dispatch [:users/set-users %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-db
 :users/set-user
 base-interceptors
 (fn [db [user]]
   (assoc-in db [:users/user] user)))

(reg-event-db
 :users/set-users
 base-interceptors
 (fn [db [users]]
   (assoc-in db [:users/list] users)))

(reg-event-db
  :users.office-hours.last-checkin/set
  base-interceptors
  (fn [db [users]]
    (assoc db :users.office-hours/last-checkin users)))

;;; ---------------------------------------------------------------------------
;;; Subscriptions

(reg-sub :users/list query)
(reg-sub :users/user query)
(reg-sub :users/profile query)
(reg-sub :users.office-hours/last-checkin query)