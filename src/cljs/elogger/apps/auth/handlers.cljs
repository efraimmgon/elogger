(ns elogger.apps.auth.handlers
  (:require
    [ajax.core :as ajax]
    [clojure.string :as string]
    [goog.crypt.base64 :as b64]
    [elogger.validation :refer [registration-errors]]
    [elogger.utils.events :refer [query base-interceptors dispatch-n]]
    [re-frame.core :as rf
     :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

;;; ---------------------------------------------------------------------------
;;; Event Handlers

;;; Helpers

(def timeout-ms
  "Max duration of a user session, with no actions."
  (* 1000 60 30))

(defn session-timer
  "If user has been inactive for the last `timeout-ms` ms, remove his
  credentials from the current session."
  [user user-event]
  (let [current-user (rf/subscribe [:identity])
        user-event (rf/subscribe [:query [:user-event]])]
    (when current-user
      (if user-event
        (do (dispatch [:assoc-in [:user-event] nil])
            (println "Setting session timer.")
            (js/setTimeout #(session-timer) timeout-ms))
        (do (println "Session timeout. Logging user out.")
            (dispatch [:auth/logout])
            (dispatch [:navigate "/"]))))))

(defn encode-auth
  [user pass]
  (->> (str user ":" pass)
       (b64/encodeString)
       (str "Basic ")))

;;; ---------------------------------------------------------------------------
;;; Main

(reg-event-fx
  :auth/login
  base-interceptors
  (fn [{:keys [db]} [params]]
    (ajax/POST
      "/api/login"
     {:headers {"Authorization"
                (encode-auth (string/trim (:users/username @params))
                             (:users/password @params))}
      :handler #(do (dispatch-n [:set-identity %] [:remove-modal])
                    (js/setTimeout session-timer timeout-ms))
      :error-handler #(dispatch [:set-error %])
      :response-format :json
      :keywords? true})
    nil))

(reg-event-fx
 :auth/logout
 base-interceptors
 (fn [_ _]
   (ajax/POST "/api/logout"
              {:handler #(dispatch [:set-identity nil])
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
  :auth/register
  base-interceptors
  (fn [_ [fields]]
    (if-let [errors (registration-errors @fields)]
      (dispatch [:ajax-error errors])
      (ajax/POST "/api/register"
                 {:params @fields
                  :handler #(dispatch-n [:set-identity %] [:remove-modal])
                  :error-handler #(dispatch [:ajax-error %])
                  :response-format :json
                  :keywords? true}))
    nil))

;;; ---------------------------------------------------------------------------
;;; Subscriptions

(reg-sub :auth/form query)
