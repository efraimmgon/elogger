(ns elogger.apps.admin.office-hours
  (:require
    [elogger.utils.components :as c]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(defn view-user-office-hours-panel-ui [])

(defn office-hours-panel-ui []
  (r/with-let [users-office-hours (rf/subscribe [:users.office-hours/last-checkin])]
    [:div.row>div.col-md-12
     [c/card
      {:header [:h4 "Jornada de trabalho"]
        
       :body
       (if-not (seq @users-office-hours)
         "Sem jornadas de trabalho para mostrar ainda."
        [:div
         [:div.clearfix]
         [:div.table-responsive.table-full-width
          [:table
           {:class "table table-hover table-striped"}
           [c/thead 
            {:class "text-center"}
            ["Id" "Nome de usuário" "Jornada iniciada?" "Último checkin" "Mais"]]
           [:tbody
            (for [u @users-office-hours]
              ^{:key (:users/id u)}
              [:tr.text-center
               ;; User id
               [:td (:users/id u)]
               ;; Username
               [:td (:users/username u)]
               ;; Is checked in?
               [:td (if (:users/is-checkedin u)
                      [:div.alert.alert-success "Sim"]
                      [:div.alert.alert-danger "Não"])]
               ;; Last checkin date
               [:td (get-in u [:users/last-checkin :office-hours/created-at])]
               ;; See more
               [:td [:a.btn.btn-primary
                     {:href (rfe/href :admin.office-hours.user/list
                                      (select-keys u [:users/id]))}
                     "+"]]])]]]])}]]))
        
      