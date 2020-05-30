(ns laconic-cms.apps.users.views
  (:require
    laconic-cms.apps.users.handlers
    [laconic-cms.utils.user :refer [full-name]]
    [laconic-cms.utils.components :refer [card form-group]]
    [laconic-cms.utils.events :refer [dispatch-n]]
    [laconic-cms.utils.forms :refer [input]]
    [laconic-cms.utils.views :refer [default-base-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(defn user-form-template [path]
  (let [user (rf/subscribe path)]
    (fn []
      [:div
       [form-group
        "Username"
        [input {:type :text
                :name (conj path :users/username)
                :class "form-control"
                :disabled true}]]
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
       ; TODO: bio
       [form-group
         "Email"
         [input {:type :email
                 :name (conj path :users/email)
                 :class "form-control"}]]])))
       ; TODO: change password

(defn edit-profile-ui []
  (r/with-let [path [:users/profile]
               profile (rf/subscribe path)]
    [default-base-ui
      [:div.wrapper
       [:div.page-header.page-header-small
        {:style {:background-image  "url('/main/img/bg.jpg')"}}]
       [:div.profile-content.section
        [:div.container
         [:div.row
          [:div.name]
          [:div.col-md-6.ml-auto.mr-auto
           [card
            {:title "Edit profile"
             :content
             [:div
              [user-form-template path]]}]]]
         [:br]
         [:div.nav-tabs-navigation.text-center
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:users/update-profile profile])}
           "Update profile"] " "
          [:button.btn.btn-danger
           {:on-click #(rf/dispatch 
                         [:users/delete-user 
                          {:users/id (:users/id @profile)
                           :handler (fn [resp]
                                      (dispatch-n [:auth/logout]
                                                  [:navigate! :home]))}])}
           "Delete account"] " "
          [:button.btn.btn-secondary
           {:on-click #(rf/dispatch [:navigate (str "/users/" (:users/id @profile))])}
           "Cancel"]]]]]]))

(defn profile-ui []
  (r/with-let [user (rf/subscribe [:identity])
               profile (rf/subscribe [:users/profile])]
    [default-base-ui
      [:div.wrapper
       [:div.page-header.page-header-small
        {:style {:background-image  "url('/main/img/bg.jpg')"}}]
       [:div.profile-content.section
        [:div.container
         [:div.row>div.col-md-12
          [:div.profile-picture
           [:div.fileinput.fileinput-new
            {:data-provides "fileinput"}
            (when (:users/img-path @profile)
              [:div.fileinput-new.img-no-padding
               [:img
                {:alt "...", :src (:users/img-path @profile)}]])
            [:div.name
             [:h4.title.text-center
              (if (seq @profile)
                (full-name @profile)
                "No user found.")]]]]]
         [:div.row
          [:div.col-md-6.ml-auto.mr-auto.text-center
           [:p (:bio @profile)]
           [:br]
           (when (= (:users/id @user) (:users/id @profile))
             [:a
              {:class "btn btn-outline-default btn-round"
               :href (rfe/href :profile/edit (select-keys @profile [:users/id]))}
               ;:on-click #(rf/dispatch [:navigate (str "/users/" (:users/id @profile) "/edit")])}
              [:i.fa.fa-cog]
              " Edit profile"])]]
         [:br]
         [:div.nav-tabs-navigation]]]]]))
