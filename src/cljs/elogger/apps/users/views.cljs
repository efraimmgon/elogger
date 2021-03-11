(ns elogger.apps.users.views
  (:require
    elogger.apps.users.handlers
    [elogger.utils.user :refer [full-name]]
    [elogger.utils.components :as c :refer [card form-group]]
    [elogger.utils.events :refer [dispatch-n]]
    [elogger.utils.forms :refer [input]]
    [elogger.utils.views :refer [default-base-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(defn update-password-modal []
  (let [current-user (rf/subscribe [:identity])
        path [:users/update-password]
        fields (rf/subscribe path)]
    (fn []
      [c/modal
       {:header [:h4 "Alterar a senha"]
        :body
        [:div
         [form-group
          "Senha atual"
          [input {:type :password
                  :name (conj path :users/old-password)
                  :class "form-control"
                  :auto-focus true}]]
         [form-group
          "Nova senha"
          [input {:type :password
                  :name (conj path :users/new-password)
                  :class "form-control"}]]
         [form-group
          "Repetir nova senha"
          [input {:type :password
                  :name (conj path :users/confirm-password)
                  :class "form-control"}]]]
        :footer
        [:div
         [:button.btn.btn-primary
          ;; TODO: on-click: check if new-password and confirm-password match
          {:on-click #(rf/dispatch 
                        [:auth/update-password 
                         {:fields (select-keys (merge @fields @current-user)
                                               [:users/id
                                                :users/username
                                                :users/old-password
                                                :users/new-password])
                          :path path}])}
                                    
                                           
          "Confirmar"] " "
         [:button.btn.btn-danger
          {:on-click #(rf/dispatch [:remove-modal])}
          "Cancelar"]]}])))

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
                 :class "form-control"}]]
       [:div.text-center
         [:button.btn.btn-info
          {:on-click #(rf/dispatch [:modal update-password-modal])}
          "Alterar a senha"]]])))

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
          [:button.btn.btn-default
           {:on-click #(rf/dispatch 
                         [:navigate! :profile/view (select-keys @profile [:users/id])])} 
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
