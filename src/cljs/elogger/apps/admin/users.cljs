(ns elogger.apps.admin.users
  (:require
   [elogger.utils.events :refer [dispatch-n <sub]]
   [elogger.utils.components :as c :refer
    [card form-group tabulate thead]]
   [elogger.utils.forms :refer [input checkbox-input radio-input]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))
   ;elogger.apps.comments.handlers))

; ------------------------------------------------------------------------------
; Components
; ------------------------------------------------------------------------------

(defn user-form-template [user]
  (let [path [:users/user]]
    (fn []
      [:div
       [form-group
        "Nome de usuário *"
        [input {:type :text,
                :name (conj path :users/username)
                :class "form-control"
                :disabled (boolean (:users/id @user))}]]
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
          "Senha *"
          [input (merge attrs {:name (conj path :users/password)
                               :class "form-control"})]])
       [form-group
        "Administrador?"
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
        "Ativo?"
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
    "Salvar usuário"]])

(defn create-user-panel-ui []
  (r/with-let [user (rf/subscribe [:users/user])]
    [:div.row>div.col-md-12
     [c/card
      {:title
       [:div
        "Novo usuário"
        [create-user-button user]]
       :body
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
    "Atualizar usuário"]])

(defn edit-user-panel-ui []
  (r/with-let [user (rf/subscribe [:users/user])]
    [:div.row>div.col-md-12
     [c/card
      {:title
       [:div
        "Editar usuário"
        [update-user-button user]]
       :body
       [:div
        [user-form-template user]
        [update-user-button user]]}]]))

(defn new-user-button []
  [:a.btn.btn-primary
   {:href (rfe/href :admin.user/create)}
   [:i.material-icons "add"]
   " Criar usuário"])

(defn users-panel-ui []
  (r/with-let [users (rf/subscribe [:users/list])]
   [:div.row>div.col-md-12
    [c/card
     {:header
      [:h4
       "Usuários"
       [:div.pull-right
        [new-user-button]]]
      :body
      (if-not (seq @users)
        "Sem usuários ainda."
        [:div
         [:div.clearfix]
         [:div.table-responsive.table-full-width
          [:table.table.table-hover.table-striped.text-center
           [c/thead ["Id" "Nome de usuário" "Email" "Último login" "Editar" "Deletar"]]
           [:tbody
            (doall
              (for [user @users]
                ^{:key (:users/id user)}
                [:tr
                 ;; Id
                 [:td (:users/id user)]
                 ;; Username
                 [:td (:users/username user)]
                 ;; Email
                 [:td (:users/email user)]
                 ;; Last login
                 [:td (:users/last-login user)]
                 ;; Edit
                 [:td
                  [:a.btn.btn-warning
                   {:href (rfe/href :admin.user/edit (select-keys user [:users/id]))}
                   [:i.material-icons "edit"]]]
                 ;; Delete
                 [:td
                  [:button.btn.btn-danger
                   {:on-click #(rf/dispatch 
                                 [:users/delete-user 
                                  {:users/id (:users/id user)
                                   :handler (fn [resp]
                                              (rf/dispatch [:users/load-users]))}])}
                   [:i.material-icons "delete"]]]]))]]]])}]]))