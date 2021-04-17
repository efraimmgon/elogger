(ns elogger.routes.services.settings
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.common :refer [q]]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as sqlcommon]
    elogger.db.sql.settings
    [elogger.routes.services.common :as common]
    [ring.util.http-response :as response]))

;;; ---------------------------------------------------------------------------
;;; DOMAIN

(s/def :office/title (s/nilable string?))
(s/def :office/description (s/nilable string?))
(s/def :office/address (s/nilable string?))
(s/def :office/latitude (s/nilable float?))
(s/def :office/longitude (s/nilable float?))

(s/def :office/Office
       (s/keys :opt [:office/title
                     :office/description
                     :office/address
                     :office/latitude
                     :office/longitude]))

(s/def :admin/Settings :office/Office)

(def office-columns
  ["office_title"
   "office_description"
   "office_address"
   "office_latitude"
   "office_longitude"])

;;; ---------------------------------------------------------------------------
;;; ROUTES

(defn get-admin-settings []
  (response/ok
    (q :settings/get-admin-settings office-columns)))

(defn update-admin-settings [params]
  (jdbc/with-transaction [tx db/*db*]
    (binding [db/*db* tx]
      (let [settings (q :settings/get-admin-settings)]
        (doseq [[k v] params]
          (let [m {:app-settings/name (str (namespace k) "_" (name k))
                   :app-settings/value v}]
            (if (seq settings)
              (q :settings/update-admin-settings m)
              (q :settings/create-admin-settings m)))))
      (response/ok
        {:result :ok}))))

