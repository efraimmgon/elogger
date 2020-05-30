(ns laconic-cms.apps.admin.users
  (:require
   [laconic-cms.utils.events :refer [dispatch-n <sub]]
   [laconic-cms.utils.components :as c :refer
    [card form-group tabulate thead]]
   [laconic-cms.utils.forms :refer [input checkbox-input radio-input]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))
   ;laconic-cms.apps.comments.handlers))

; ------------------------------------------------------------------------------
; Components
; ------------------------------------------------------------------------------

(defn user-form-template [user]
  (let [path [:users/user]]
    (fn []
      [:div
       [form-group
        "Username *"
        [input {:type :text,
                :name (conj path :users/username)
                :class "form-control"
                :disabled (boolean (:users/id @user))}]]
       [form-group
        "First name"
        [input {:type :text
                :name (conj path :users/profile :profile/first-name)
                :class "form-control"}]]
       [form-group
        "Last name"
        [input {:type :text
                :name (conj path :users/profile :profile/last-name)
                :class "form-control"}]]
       [form-group
        "Email"
        [input {:type :email
                :name (conj path :users/email)
                :class "form-control"}]]
       (let [attrs (if (:users/id @user)
                     {:type :text, 
                      :disabled true}
                     {:type :password})]
         [form-group
          "Password *"
          [input (merge attrs {:name (conj path :users/password)
                               :class "form-control"})]])
       [form-group
        "Admin?"
        [radio-input
         {:name (conj path :users/admin)
          :value true
          :label "Yes"}]
        [radio-input
         {:name (conj path :users/admin)
          :label "No"
          :value false
          :default-checked true}]]
       [form-group
        "Active?"
        [radio-input
         {:name (conj path :users/is-active)
          :value true
          :default-checked true
          :label "Yes"}]
        [radio-input
         {:name (conj path :users/is-active)
          :label "No"
          :value false}]]])))

(defn create-user-button [user]
  [:div.pull-right
   [:button.btn.btn-primary
    {:on-click #(rf/dispatch 
                  [:users/create-user
                   {:doc @user
                    :handler (fn [resp]
                               (rf/dispatch [:navigate! :admin.users/list]))}])}
    "Save user"]])

(defn create-user-panel-ui []
  (r/with-let [user (rf/subscribe [:users/user])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "New user"
        [create-user-button user]]
       :content
       [:div
        [user-form-template user]
        [create-user-button user]]}]]))

(defn update-user-button [user]
  [:div.pull-right
   [:button.btn.btn-primary
    {:on-click #(rf/dispatch 
                  [:users/edit-user
                   {:doc @user
                    :handler (fn [resp]
                               (rf/dispatch [:navigate! :admin.users/list]))}])}
    "Update user"]])

(defn edit-user-panel-ui []
  (r/with-let [user (rf/subscribe [:users/user])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "Edit user"
        [update-user-button user]]
       :content
       [:div
        [user-form-template user]
        [update-user-button user]]}]]))

(defn new-user-button []
  [:a.btn.btn-primary
   {:href (rfe/href :admin.user/create)}
   [:i.material-icons "add"]
   " Create user"])

(defn users-panel-ui []
  (r/with-let [users (rf/subscribe [:users/list])]
   [:div.row>div.col-md-12
    [card
     {:title
      [:div
       "Users"
       [:div.pull-right
        [new-user-button]]]
      :content
      (if-not (seq @users)
        "No users yet."
        [:div
         [:div.clearfix]
         [:div.table-responsive.table-full-width
          [tabulate
           [:users/id :users/username :users/email :users/last-login :users/edit :users/delete]
           (doall
             (for [user @users]
               (assoc user
                      :users/last-login
                      (or (:users/last-login user) "-")

                      :users/edit
                      [:a.btn.btn-warning
                       {:href (rfe/href :admin.user/edit {:users/id (:users/id user)})}
                       [:i.material-icons "edit"]]

                      :users/delete
                      [:button.btn.btn-danger
                       {:on-click #(rf/dispatch 
                                     [:users/delete-user 
                                      {:users/id (:users/id user)
                                       :handler (fn [resp]
                                                  (rf/dispatch [:users/load-users]))}])}
                       [:i.material-icons "delete"]])))
           {:class "table table-hover table-striped"}]]])}]]))
