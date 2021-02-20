(ns elogger.routes.services.auth
  (:require
    [buddy.hashers :as hashers]
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as sqlcommon]
    [elogger.db.sql.user :as db]
    ;; Register specs:
    elogger.routes.services.user
    [elogger.routes.services.common :as common]
    [ring.util.http-response :as response]))

(s/def :auth/authorization string?)

(s/def :users/password-confirm string?)

(s/def :auth/UserRegistration
       (s/keys :req  [:users/username
                      :users/password
                      :users/password-confirm]))

; ------------------------------------------------------------------------------
; Helpers
; ------------------------------------------------------------------------------  

(defn update-last-login! [user]
  (db/update-user!
    (assoc user :users/last-login (common/now))))

(defn decode-auth [encoded]
  (let [auth (second (.split encoded " "))]
    (-> (.decode (java.util.Base64/getDecoder) auth)
        (String. (java.nio.charset.Charset/forName "UTF-8"))
        (.split ":"))))

(defn authenticate [[username password]]
  (when-let [user (db/get-user-by-username username)]
    (when (hashers/check password (:users/password user))
      user)))

; ------------------------------------------------------------------------------
; Main
; ------------------------------------------------------------------------------

(defn register!
  "Create a new user and profile, and log him in."
  [{:keys [session]} user]
  (db/create-user-with-profile!
    (-> user
        (select-keys sqlcommon/user-columns)
        (assoc :users/last-login (common/now))
        (update :users/password hashers/derive)))
  (let [user (db/get-user-by-username (:users/username user))]
    (-> user
        response/ok
        (assoc-in [:session :identity] user))))

(defn login!
  "Update user's last login and return the record if user exists and
  the credentials match. Otherwise, return response/unauthorized."
  [{:keys [session]} auth]
  (if-let [user (-> auth decode-auth authenticate)]
    (let [now (common/now)]
      (db/update-user!
        (-> (select-keys user sqlcommon/user-columns)
            (assoc :users/last-login now)))
      (-> user
          (assoc :users/last-login now)
          response/ok
          (assoc-in [:session :identity] user)))
    (response/unauthorized {:result :unauthorized
                            :message "login failure"})))

(defn logout!
  "Log the user out by resetting the session to nil."
  []
  (-> {:result :ok}
      response/ok
      (assoc :session nil)))

(defn checkin!
  "Checks the user into his office hours."
  [params]
  (let [user (db/get-user (:office-hours/user-id params))]
    (if (:users/is-checkedin user)
      (response/precondition-failed {:result :precondition-failed
                                     :message "O turno já foi iniciado!"})
      (response/ok (db/checkin! params)))))

(defn checkout!
  "Checks the user out of his office hours."
  [params]
  (let [user (db/get-user (:office-hours/user-id params))]
    (if (:users/is-checkedin user)
      (response/ok (db/checkout! params))
      (response/precondition-failed 
        {:result :precondition-failed
         :message "Você ainda não iniciou sua jornada!"}))))

(comment
  (register! {:session nil}
             {:users/username "newuser14"
              :users/password "foobar123"})

  (s/valid? :users/User
            (:body
              (register! {:session nil}
                         {:users/username "newuser16"
                          :users/password "foobar123"}))))