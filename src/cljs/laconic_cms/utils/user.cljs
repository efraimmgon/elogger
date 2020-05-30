(ns laconic-cms.utils.user)

(defn full-name 
  "If the user has a first and last names returns their concatenation,
  otherwise returns the username."
  [{:users/keys [profile username] :as user}]
  (let [{:profile/keys [first-name last-name]} profile]
    (if (seq user)
      (or (and first-name last-name 
               (str first-name " " last-name))
          username)
      "anonymous")))

(defn admin? [user]
  (:users/admin user))

(defn author? [x user]
  (and (seq x) (seq user)
       (= (:users/id x)
          (:users/id user))))
