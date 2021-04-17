(ns elogger.apps.auth.views
  (:require
   elogger.apps.auth.handlers
   [elogger.utils.components :as c :refer
    [form-group modal]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [elogger.utils.forms :refer [input]]
   [elogger.utils.views :refer [login-base-ui]]))


(defn stats-modal []
  (let [stats (rf/subscribe [:users.office-hours/stats])]
    (fn []
      [c/modal
       {:header [:h4 "Horas trabalhadas por mês"]
        :body
        [:div
         (if (seq @stats)
          [:table.table.table-striped.text-center
           [c/thead ["Ano" "Mês" "Horas"]]
           [:tbody
            (doall
              (for [[[year month] hours] @stats]
                ^{:key (gensym)}
                [:tr
                 [:td year]
                 [:td month]
                 [:td hours]]))]]
          "Nada para mostrar ainda.")]
        :footer
        [:div
         [:button.btn.btn-default 
          {:on-click #(rf/dispatch [:remove-modal])}
          "Voltar"]]}])))


(defn login-form-body [path]
  [:div.card-body
   [:div.input-group
    [:div.input-group-prepend
     [:span.input-group-text [:i.material-icons "face"]]]
    [input {:type :text
            :name (conj path :users/username)
            :class "form-control"
            :auto-focus true
            :placeholder "Nome de usuário"}]]
   [:div.input-group
    [:div.input-group-prepend
     [:span.input-group-text
      [:i.material-icons "lock_outline"]]]
    [input {:type :password
            :name (conj path :users/password)
            :class "form-control"
            :placeholder "Senha"}]]])

(defn login-form-ui [current-user]
  (let [path [:auth/form]
        fields (rf/subscribe path)]
    (fn []
      [:div.card.card-login
       [:form.form
        [:div.card-header.card-header-primary.text-center
         [:h4.card-title "Login"]]
        [login-form-body path]
        [:div.footer.text-center
         [:a.btn.btn-primary.btn-link.btn-wd.btn-lg
          {:on-click #(rf/dispatch-sync
                        [:auth/login {:params fields
                                      :path path}])}
          "Entrar"]]]])))

(defn checkin-checkout-ui [current-user]
  (let [path [:auth/form]
        fields (rf/subscribe path)
        loading-msg? (rf/subscribe [:query [:auth.check.notify/loading]])
        geolocation-off? (rf/subscribe [:query [:auth.checkin/geolocation-off?]])
        error (rf/subscribe [:query [:auth.checkin/error]])]
    (rf/dispatch [:users.office-hours/load-stats (:users/id @current-user)])
    (fn []
      [:div.card.card-login
       [:form.form
        [:div.card-header.card-header-primary.text-center
         [:h4.card-title "Você está logado"]]
         ;; BODY
        [:div.card-body
          [:h5.text-center (str (js/Date.))]
          [:h5.text-center "Jornada: "
           (if (:users/is-checkedin @current-user)
             [:span.badge.badge-pill.badge-success "Iniciada"]
             [:span.badge.badge-pill.badge-danger "Terminada"])]
          (when @geolocation-off?
            [:div.alert.alert-danger
             "Para realizar o checkin ative o serviço de localização do seu 
             aparelho e tente novamente!"])
          (when @loading-msg?
            [:div.alert.alert-warning
             @loading-msg?])]
        [:div.footer.text-center
         [:a.btn.btn-link.btn-info.btn-wd.btn-lg
          {:on-click #(rf/dispatch [:modal stats-modal])}
          "Histórico"]
         (if (:users/is-checkedin @current-user)
           [:a.btn.btn-primary.btn-link.btn-wd.btn-lg.btn-danger
            {:on-click #(rf/dispatch-sync
                          [:auth/checkout! current-user])
             :disabled @geolocation-off?}
            "Terminar o turno"]
           [:a.btn.btn-primary.btn-link.btn-wd.btn-lg.btn-success
            {:on-click #(rf/dispatch-sync
                          [:auth/checkin! current-user])
             :disabled @geolocation-off?}
            "Iniciar o turno"])]]])))


(defn home-ui []
  (let [current-user (rf/subscribe [:identity])]
    (fn []
      [login-base-ui
       [:div.page-header.header-filter
        {:style {"backgroundImage" "url('/img/bg7.jpg')"
                 "backgroundSize" "cover"
                 "backgroundPosition" "top center"}}
        [:div.container
         [:div.row
          [:div.col-lg-4.col-md-6.ml-auto.mr-auto
           (if @current-user
             [checkin-checkout-ui current-user]
             [login-form-ui current-user])]]]]])))


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
