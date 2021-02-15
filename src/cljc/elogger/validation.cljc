(ns elogger.validation
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]))

(defn registration-errors [{:users/keys [password-confirm] :as params}]
  (first
   (b/validate
    params
    :users/username v/required
    :users/password [v/required
                     [v/min-count 7 :message "password must contain at least 8 characters"]
                     [= password-confirm :message "re-entered password does not match"]])))
