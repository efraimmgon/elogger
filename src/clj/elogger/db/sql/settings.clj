(ns elogger.db.sql.settings
  (:require 
    [elogger.db.common :refer [q]]
    [elogger.db.core :as db]
    [next.jdbc :as jdbc]))

(defn in-helper [coll]
  (str "("
       (->> (repeat "?")
            (take (count coll))
            (clojure.string/join ", "))
       ")"))
  
        

(defmethod q :settings/get-admin-settings 
  [_ names]
  (db/execute!
    (into
      [(str
         "SELECT * FROM app_settings s WHERE
         s.name IN " (in-helper names) "")]
      names)))


(defmethod q :settings/create-admin-settings
  [_ params]
  (db/insert! "app_settings" params))

(defmethod q :settings/update-admin-settings
  [_ params]
  (db/update! "app_settings" 
              params 
              (select-keys params [:app-settings/name])))
              
      
