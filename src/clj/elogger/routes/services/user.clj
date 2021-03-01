(ns elogger.routes.services.user
  (:require
    [buddy.hashers :as hashers]
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.user :as db]
    [elogger.routes.services.common :as common]
    [elogger.db.sql.common :as sqlcommon]
    [ring.util.http-response :as response]))

;;; ---------------------------------------------------------------------------
;;; FIELDS DOMAIN

(s/def :users/id ::common/id)
(s/def :users/username string?)
(s/def :users/email (s/nilable
                      (s/and string?
                             #(re-matches common/email-regex %))))
(s/def :users/password string?)
(s/def :users/admin (s/nilable boolean?))
(s/def :users/is-active (s/nilable boolean?))
(s/def :users/is-checkedin (s/nilable boolean?))
(s/def :users/created-at ::common/date)
(s/def :users/updated-at ::common/date)

(s/def :profile/id ::common/id)
(s/def :profile/user-id ::common/id)
(s/def :profile/first-name (s/nilable string?))
(s/def :profile/last-name (s/nilable string?))
(s/def :profile/bio (s/nilable string?))
(s/def :profile/created-at ::common/date)
(s/def :profile/updated-at ::common/date)

(s/def :office-hours/id ::common/id)
(s/def :office-hours/user-id :users/id)
(s/def :office-hours/status #{"checkin" "checkout"})
(s/def :office-hours/created-at ::common/date)
(s/def :office-hours/updated-at ::common/date)
(s/def :office-hours/user-agent string?)
(s/def :office-hours/lat float?)
(s/def :office-hours/lng float?)

;;; ---------------------------------------------------------------------------
;;; OFFICE HOURS DOMAIN

(s/def :office-hours/OfficeHours
       (s/keys :req [:office-hours/id
                     :office-hours/user-id
                     :office-hours/created-at
                     :office-hours/updated-at
                     :office-hours/user-agent
                     :office-hours/lat
                     :office-hours/lng]))

(s/def :users/last-checkin :office-hours/OfficeHours)

(s/def :office-hours/office-hours
       (s/* :office-hours/OfficeHours))

;;; ---------------------------------------------------------------------------
;;; PROFILE DOMAIN

(s/def :profile/Profile
       (s/keys :req [:profile/id
                     :profile/user-id
                     :profile/created-at
                     :profile/updated-at]
               :opt [:profile/first-name
                     :profile/last-name
                     :profile/bio
                     :profile/profile-picture-id]))

(s/def :profile/profiles
       (s/* :profile/Profile))

;;; ---------------------------------------------------------------------------
;;; USER DOMAIN

(s/def :users/profile (s/nilable :profile/Profile))

(s/def :users/User
       (s/keys :req  [:users/id
                      :users/username
                      :users/email
                      :users/password
                      :users/admin
                      :users/is-active
                      :users/is-checkedin
                      :users/created-at
                      :users/updated-at
                      :users/profile]
               :opt [:users/last-checkin]))

(s/def :users/users (s/* :users/User))


;;; ---------------------------------------------------------------------------
;;; CORE

(defn any-granted? 
  "User authorization: only admin and own user."
  [identity user-id]
  (when identity
    (or (:users/admin identity)
        (= (:users/id identity)
           (:users/id (db/get-user-by-id user-id))))))

(defn create-user!
  "Create a user record."
  [params]
  (db/create-user!
    (update params :users/password hashers/derive))
  (response/ok
    {:result :ok}))

(defn create-user-with-profile!
  "Create a user and profile record."
  [params]
  (db/create-user-with-profile!
    (update params :users/password hashers/derive))
  (response/ok
    {:result :ok}))

(defn get-users
  "Return all user recods."
  []
  (response/ok
    (db/get-users)))

(defn get-user
  "Return a user record by id."
  [id]
  (response/ok
    (db/get-user-by-id id)))

(defn get-user-office-hours-by-user-id
  "Return all the user's info and office hours rows."
  [user-id]
  (response/ok
    (db/get-user-office-hours-by-user-id user-id)))

(defn get-users-office-hours-last-checkin
  "Return all users info with their last office hours checkin row."
  []
  (response/ok
    (db/get-users-office-hours-last-checkin)))

(defn update-user!
  "Update a user record by id."
  [params]
  (db/update-user!
    (assoc params :users/updated-at (common/now)))
  (response/ok
    {:result :ok}))

(defn update-user-with-profile!
  "Update a user and profile record by id."
  [params]
  (let [now (common/now)
        profile-params (:users/profile params)]
    (cond-> params
      (seq profile-params)
      (-> (update :users/profile dissoc :profile/created-at)
          (assoc-in [:users/profile :profile/updated-at] now))
    
      true 
      (-> (assoc :users/updated-at now)
          db/update-user-with-profile!)))
  (response/ok
    {:result :ok}))

(defn delete-user!
  "Delete a user record by id."
  [id]
  (db/delete-user! id)
  (response/ok
    {:result :ok}))

(comment 
  (s/explain
    :users/User
    (get-user-by-id 10))

  (s/valid?
    :users/users
    (get-all-users)))