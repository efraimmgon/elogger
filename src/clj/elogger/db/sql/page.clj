(ns elogger.db.sql.page
  (:require 
    [clojure.spec.alpha :as s]
    [elogger.db.core :as db]
    [elogger.db.pathom :refer [parser]]
    [elogger.db.sql.common :as common]))

(defn get-page-by-id [id]
  (let [page (parser {[:page/id id] common/page-query})]
    (when (s/valid? :page/Page page)
      page)))

(defn get-pages []
  (:pages/list
    (parser {[:pages/all] [{:pages/list common/page-query}]})))

(defn create-page!
  "Creates a new page record."
  [params]
  (db/insert! "page" params))

(defn update-page!
  "Updates an existing page record, by page_id."
  [{:keys [page/id] :as params}]
  (db/update! "page"
              (dissoc params :page/id)
              (select-keys params [:page/id])))

(defn delete-page!
  "Deletes a page record given the page id."
  [id]
  (db/delete! "page" {:id id}))

