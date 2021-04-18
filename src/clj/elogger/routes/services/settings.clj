(ns elogger.routes.services.settings
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.core :as db]
    [elogger.db.common :refer [q]]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as sqlcommon]
    elogger.db.sql.settings
    [elogger.routes.services.common :as common]
    [next.jdbc :as jdbc]      
    [ring.util.http-response :as response]))

;;; ---------------------------------------------------------------------------
;;; DOMAIN

(s/def :app-settings/office-title (s/nilable string?))
(s/def :app-settings/office-description (s/nilable string?))
(s/def :app-settings/office-address (s/nilable string?))
(s/def :app-settings/office-latitude (s/nilable string?))
(s/def :app-settings/office-longitude (s/nilable string?))

(s/def :app-settings/Office
       (s/keys :opt [:app-settings/office-title
                     :app-settings/office-description
                     :app-settings/office-address
                     :app-settings/office-latitude
                     :app-settings/office-longitude]))

(s/def :admin/Settings (s/nilable :app-settings/Office))

(def office-columns
  ["office-title"
   "office-description"
   "office-address"
   "office-latitude"
   "office-longitude"])

;;; ---------------------------------------------------------------------------
;;; ROUTES

(defn get-admin-settings []
  (response/ok
    (when-let [rows (seq (q :settings/get-admin-settings office-columns))]
      (->> rows
           (map (fn [{:app-settings/keys [name value]}] 
                  [(keyword "app-settings" name) value]))
           (into {})))))
      

(defn update-admin-settings [params]
  (prn 'params params)
  (jdbc/with-transaction [tx db/*db*]
    (binding [db/*db* tx]
      (let [settings (q :settings/get-admin-settings office-columns)]
        (doseq [[k v] params]
          (let [m {:app-settings/name (name k)
                   :app-settings/value v}]
            (if (seq settings)
              (q :settings/update-admin-settings m)
              (q :settings/create-admin-settings m)))))
      (response/ok
        {:result :ok}))))

