(ns elogger.apps.users.views
  (:require
    elogger.apps.users.handlers
    [elogger.utils.user :refer [full-name]]
    [elogger.utils.components :refer [card form-group]]
    [elogger.utils.events :refer [dispatch-n]]
    [elogger.utils.forms :refer [input]]
    [elogger.utils.views :refer [default-base-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(defn user-form-template [path]
  (let [user (rf/subscribe path)]
    (fn []
      [:div
       [form-group
        "Nome de usuário"
        [input {:type :text
                :name (conj path :users/username)
                :class "form-control"
                :disabled true}]]
       [form-group
         "Primeiro nome"
         [input {:type :text
                 :name (conj path :users/profile :profile/first-name)
                 :class "form-control"}]]
       [form-group
         "Sobrenome"
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
            {:title "Editar"
             :body
             [:div
              [user-form-template path]]}]]]
         [:br]
         [:div.nav-tabs-navigation.text-center
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:users/update-profile profile])}
           "Salvar"] " "
          [:button.btn.btn-danger
           {:on-click #(rf/dispatch 
                         [:users/delete-user 
                          {:users/id (:users/id @profile)
                           :handler (fn [resp]
                                      (dispatch-n [:auth/logout]
                                                  [:navigate! :home]))}])}
           "Deletar"] " "
          [:button.btn.btn-secondary
           {:on-click #(rf/dispatch [:navigate (str "/users/" (:users/id @profile))])}
           "Cancelar"]]]]]]))

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
                "Usuário não encontrado.")]]]]]
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
              " Editar perfil"])]]
         [:br]
         [:div.nav-tabs-navigation]]]]]))
