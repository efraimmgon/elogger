(ns elogger.apps.auth.handlers
  (:require
    [ajax.core :as ajax]
    [clojure.string :as string]
    [goog.crypt.base64 :as b64]
    [elogger.validation :refer [registration-errors]]
    [elogger.apps.admin.utils :refer [distance]]
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
    (when @current-user
      (if @user-event
        (do (dispatch [:assoc-in [:user-event] nil])
            (println "Setting session timer.")
            (js/setTimeout #(session-timer) timeout-ms))
        (do (println "Session timeout. Logging user out.")
            (dispatch [:auth/logout])
            (dispatch [:navigate! :home]))))))

(defn encode-auth
  [& args]
  (->> args
       (interpose ":")
       (apply str)
       (b64/encodeString)
       (str "Basic ")))


;;; ---------------------------------------------------------------------------
;;; Main

(defn loading-msg [msg]
  (rf/dispatch [:assoc-in [:auth.check.notify/loading] msg]))

(defn geo-loc-fail []
  (rf/dispatch [:assoc-in [:auth.checkin/geolocation-off?] true]))

(defn clear-geo-loc-msg []
  (rf/dispatch [:assoc-in [:auth.checkin/geolocation-off?] nil]))

(defn get-current-location [geolocation success fail]
  (loading-msg "Carreando, aguarde...")
  (.getCurrentPosition geolocation success fail))

(defn gcl-success [{:keys [location-timeout url handler current-user]}]
  (fn [position]
    (js/clearTimeout location-timeout)
    (let [settings (rf/subscribe [:admin/settings])
          lat (-> position .-coords .-latitude)
          lng (-> position .-coords .-longitude)
          d (distance ((juxt :office/latitude :office/longitude) @settings)
                      [lat lng])]
      (if (or ;; The checkedin user can checkout from any place.
              (:users/is-checkedin @current-user) 
              ;; Minimum distance for the user to checkin.
              (<= d 500))
        (ajax/POST url
                   {:params {:office-hours/user-id (:users/id @current-user)
                             :office-hours/lat lat
                             :office-hours/lng lng}
                    :handler handler
                    :error-handler #(dispatch [:set-error (str (:response %))])
                    :finally #(loading-msg nil)
                    :response-format :json
                    :keywords? true})
        (loading-msg (str "Você não pode iniciar a jornada sem antes chegar ao "
                          "local de trabalho."))))))

(defn gcl-fail [location-timeout]
  (fn [error]
    (js/clearTimeout location-timeout)
    (geo-loc-fail)))

(reg-event-fx
  :auth/checkin!
  base-interceptors
  (fn [{:keys [db]} [current-user]]
    (clear-geo-loc-msg)
    (if-let [geolocation (.-geolocation js/navigator)]
      (let [location-timeout (js/setTimeout geo-loc-fail 10000)]
        (get-current-location 
          geolocation 
          (gcl-success 
            {:location-timeout location-timeout 
             :current-user current-user
             :url "/api/checkin" 
             :handler #(dispatch [:assoc-in [:identity :users/is-checkedin] true])})
          (gcl-fail location-timeout)))
      (do (loading-msg nil)
          (geo-loc-fail)))
    nil))

(reg-event-fx
  :auth/checkout!
  base-interceptors
  (fn [_ [current-user]]
    (clear-geo-loc-msg)
    (if-let [geolocation (.-geolocation js/navigator)]
      (let [location-timeout (js/setTimeout geo-loc-fail 10000)]
        (get-current-location 
          geolocation 
          (gcl-success 
            {:location-timeout location-timeout 
             :current-user current-user
             :url "/api/checkout" 
             :handler #(dispatch [:assoc-in [:identity :users/is-checkedin] false])})
          (gcl-fail location-timeout)))
      (geo-loc-fail))
    nil))

(reg-event-fx
  :auth/login
  base-interceptors
  (fn [{:keys [db]} [{:keys [params path]}]]
    (ajax/POST "/api/login"
     {:headers {"Authorization"
                (encode-auth (string/trim (:users/username @params))
                             (:users/password @params))}
      :handler (fn [user]
                 (dispatch-n [:set-identity user] [:assoc-in path nil]))
                 ;(js/setTimeout session-timer timeout-ms))
      :error-handler #(dispatch [:set-error %])
      :response-format :json
      :keywords? true})
    nil))

(reg-event-fx
  :auth/update-password
  base-interceptors
  (fn [_ [{:keys [fields path]}]]
    (ajax/POST (str "/api/update-password")
              {:params fields
               :handler (fn [pwrd]
                          (rf/dispatch [:assoc-in [:identity :users/password] pwrd])
                          (rf/dispatch [:assoc-in path nil]) ; clear form
                          (rf/dispatch [:remove-modal]))
               :error-handler #(rf/dispatch [:ajax-error %])})
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