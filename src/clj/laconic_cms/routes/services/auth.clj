(ns laconic-cms.routes.services.auth
  (:require
    [buddy.hashers :as hashers]
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [laconic-cms.db.pathom :refer [parser]]
    [laconic-cms.db.sql.common :as sqlcommon]
    [laconic-cms.db.sql.user :as db]
    ;; Register specs:
    laconic-cms.routes.services.user
    [laconic-cms.routes.services.common :as common]
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

(defn handle-registration-error [e]
  (if (and (instance? java.sql.SQLException e)
           (-> e
               .getNextException
               .getMessage
               (.startsWith "ERROR: duplicate key value")))
    (response/precondition-failed
      {:result :error
       :message "user with the selected username already exists"})
    (do (log/error e)
        (response/internal-server-error
          {:result :error
           :message "server error occurred while adding the user"}))))

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
  "Creates a new user and profile, and logs him in."
  [{:keys [session]} user]
  (try
    (db/create-user-with-profile!
      (-> user
          (select-keys sqlcommon/user-columns)
          (assoc :users/last-login (common/now))
          (update :users/password hashers/derive)))
    (let [user (db/get-user-by-username (:users/username user))]
      (-> user
          response/ok
          (assoc-in [:session :identity] user)))))

(defn login!
  "Update user's last login and return the record if user exists and
  the credentials match. Otherwise, return response/unauthorized."
  [{:keys [session]} auth]
  (if-let [user (-> auth decode-auth authenticate)]
    (do ;; Update the last-login field:
      (db/update-user!
        (-> (select-keys user sqlcommon/user-columns)
            (assoc :users/last-login (common/now))))
      ;; Return the user's identity assoc'ed to the session:
      (-> user
          response/ok
          (assoc-in [:session :identity] user)))
    (response/unauthorized {:result :unauthorized
                            :message "login failure"})))

(defn logout!
  "Logs the user out by resetting the session to nil."
  []
  (-> {:result :ok}
      response/ok
      (assoc :session nil)))


(comment
  (register! {:session nil}
             {:users/username "newuser14"
              :users/password "foobar123"})

  (s/valid? :users/User
            (:body
              (register! {:session nil}
                         {:users/username "newuser16"
                          :users/password "foobar123"}))))