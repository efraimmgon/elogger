(ns elogger.routes.services.settings
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as sqlcommon]
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

;;; ---------------------------------------------------------------------------
;;; ROUTES

(defn read-resource [filename]
  (-> filename
      clojure.java.io/resource
      slurp
      clojure.edn/read-string))

(defn write-resource [filename content]
  (spit 
    (clojure.java.io/resource filename)
    (with-out-str 
      (clojure.pprint/pprint
        content))))

(defn get-admin-settings []
  (response/ok
    (read-resource "settings.edn")))

(defn update-admin-settings [params]
  (write-resource "settings.edn" params)
  (response/ok
    {:result :ok}))

(comment
  (read-resource "settings.edn")
  (write-resource "settings.edn" {}))