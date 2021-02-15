(ns elogger.routes.services.page
  (:require
    [clojure.spec.alpha :as s]
    [clojure.tools.logging :as log]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as sqlcommon]
    [elogger.db.sql.page :as db]
    [elogger.routes.services.common :as common]
    [elogger.utils :refer [string->date]]
    [ring.util.http-response :as response]))

;;; ---------------------------------------------------------------------------
;;; DOMAIN

(s/def :page/id ::common/id)
(s/def :page/title string?)
(s/def :page/subtitle (s/nilable string?))
(s/def :page/content string?)
(s/def :page/author-id ::common/id)
(s/def :page/featured-img-id (s/nilable ::common/id))
(s/def :page/published-at ::common/date)
(s/def :page/expires-at (s/nilable ::common/date))
(s/def :page/status ::common/status)
(s/def :page/created-at ::common/date)
(s/def :page/updated-at ::common/date)
(s/def :page/author :users/User)

(s/def :page/Page
       (s/keys :req  [:page/id
                      :page/title
                      :page/subtitle
                      :page/content
                      :page/author-id
                      :page/author
                      :page/featured-img-id
                      :page/published-at
                      :page/expires-at
                      :page/status
                      :page/created-at
                      :page/updated-at]))

(s/def :page/pages (s/* :page/Page))


;;; ---------------------------------------------------------------------------
;;; CORE

(defn any-granted? [identity page-id]
  (when identity
    (or (:users/admin identity)
        (= (:users/id identity)
           (-> page-id
               db/get-page-by-id
               :page/author-id)))))

(defn get-page
  "Get a page record by id."
  [id]
  (response/ok
    (db/get-page-by-id id)))

(defn get-pages
  "Get all page records by id."
  []
  (response/ok
    (db/get-pages)))

(defn create-page!
  "Create a page record."
  [params]
  ; TODO: check that image file exists
  (db/create-page! params)
  (response/ok
    {:result :ok}))
    

(defn update-page!
  "Update a page record by id."
  [params]
  ; TODO: check that image file exists
  (db/update-page! 
    (-> params
        (dissoc :users/id) ; We don't want to overwrite the author, in case it's a different user
        (assoc :page/updated-at (common/now))
        (update :page/published-at string->date)))
      
  (response/ok
    {:result :ok}))

(defn delete-page!
  "Delete a page record by id."
  [id]
  (db/delete-page! id)
  (response/ok
    {:result :ok}))

(comment
  
  (s/valid?
    :page/Page
    (:body (get-page 1)))
  
  (->> (get-page 1)
       :body))