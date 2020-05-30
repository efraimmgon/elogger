(ns laconic-cms.routes.services.file
  (:require
    [clojure.spec.alpha :as s]
    [laconic-cms.routes.services.common :as common]))

(s/def :file/id ::common/id)
(s/def :file/user-id :users/id)
(s/def :file/type string?)
(s/def :file/name string?)
(s/def :file/owner :users/User)
;; TODO:
(s/def :file/data any?)
(s/def :file/created-at ::common/date)
(s/def :file/updated-at ::common/date)

(s/def :file/File
       (s/keys :req [:file/id
                     :file/user-id
                     :file/type
                     :file/name
                     :file/owner
                     :file/data
                     :file/created-at
                     :file/updated-at]))

(s/def :profile/profile-picture-id (s/nilable :file/id))
