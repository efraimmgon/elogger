(ns elogger.apps.auth.views
  (:require
   elogger.apps.auth.handlers
   [elogger.utils.components :as c :refer
    [form-group modal]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [elogger.utils.forms :refer [input]]))

(defn login-form [path]
  [:div
    [form-group
     "Username"
     [input {:type :text
             :name (conj path :users/username)
             :class "form-control"
             :auto-focus true}]]
    [form-group
     "Password"
     [input {:type :password
             :name (conj path :users/password)
             :class "form-control"}]]])

(defn login-modal []
  (let [path [:auth/form]
        fields (rf/subscribe path)]
    (fn []
      [c/modal
       {:attrs {:on-key-down (c/on-key-handler
                              {"Enter" #(rf/dispatch [:auth/login fields])
                               "Escape" #(rf/dispatch [:remove-modal])})}
        :header "Login"
        :body
         ;; TODO: validation
         [login-form path]
        :footer
         [:div.pull-left
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:auth/login fields])}
           "Login"] " "
          [:button.btn.btn-danger
           {:on-click #(rf/dispatch [:remove-modal])}
           "Cancel"]]}])))


(defn register-form [path]
  [:div
   [form-group
     "Username"
     [input {:type :text
             :name (conj path :users/username)
             :class "form-control"
             :auto-focus true}]]
   [form-group
     "Password"
     [input {:type :password
             :name (conj path :users/password)
             :class "form-control"}]]

   [form-group
     "Confirm Password"
     [input {:type :password
             :name (conj path :users/password-confirm)
             :class "form-control"}]]])

(defn register-modal []
  (r/with-let [path [:auth/form]
               fields (rf/subscribe path)]
    [c/modal
     {:attrs {:on-key-down
               (c/on-key-handler
                 {"Enter" #(rf/dispatch [:auth/register fields])
                  "Escape" #(rf/dispatch [:remove-modal])})}
      :header "Register"
      :body
       ;; TODO: validation
       [register-form path]
      :footer
       [:div
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:auth/register fields])}
         "Register"] " "
        [:button.btn.btn-danger
         {:on-click #(rf/dispatch [:remove-modal])}
         "Cancel"]]}]))
