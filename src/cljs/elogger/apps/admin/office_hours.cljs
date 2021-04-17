(ns elogger.apps.admin.office-hours
  (:require
    [elogger.apps.admin.utils :refer [admin-page-ui]]
    [elogger.utils.components :as c]
    [elogger.utils.views :refer [page-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(defn view-user-office-hours-panel-ui []
  (r/with-let [user-oh (rf/subscribe [:users.office-hours/all])
               stats (rf/subscribe [:users.office-hours/stats])
               current-page (r/atom 0)]
    [:div.row>col-md-12
     
     [c/card
      {:title (str (:users/username @user-oh) " > Jornadas de trabalho")
       :body
       (if-not (seq (:users/office-hours @user-oh))
         "Nada para mostrar ainda."
         (let [part-user-oh (c/partition-links 10 (:users/office-hours @user-oh))]
           [:div
             [:h5.text-center "Horas trabalhadas por mês"]
             [:table.table.table-striped.text-center
              [c/thead ["Ano" "Mês" "Horas"]]
              [:tbody
               (doall
                 (for [[[year month] hours] @stats]
                   [:tr
                    [:td year]
                    [:td month]
                    [:td hours]]))]]
             [c/pager (count part-user-oh) current-page]
             [:table.table.table-striped.text-center
              [c/thead ["id" "status" "latitude" "longitude" "data/hora" "user-agent"]]
              [:tbody
               (doall
                 (for [{:office-hours/keys [id status lat lng created-at user-agent]}
                       (part-user-oh @current-page)]
                   ^{:key id}
                   [:tr
                     ;; id
                     [:td id]
                     ;; status
                     (if (#{"checkin"} status)
                       [:td.alert.alert-success status]
                       [:td.alert.alert-danger status])
                     ;; lat
                     [:td lat]
                     ;; lng
                     [:td lng]
                     ;; created-at
                     [:td created-at]
                     ;; user-agent
                     [:td user-agent]]))]]
             [c/pager (count part-user-oh) current-page]]))}]]))


(defn office-hours-panel-ui []
  (r/with-let [users-office-hours (rf/subscribe [:users.office-hours/last-checkin])
               current-page (r/atom 0)]
    [:div.row>div.col-md-12
     [c/card
      {:header [:h4 "Jornada de trabalho"]
        
       :body
       (if-not (seq @users-office-hours)
         "Sem jornadas de trabalho para mostrar ainda."
         (let [part-users-office-hours (c/partition-links 10 @users-office-hours)]
           [:div
            [c/pager (count part-users-office-hours) current-page]
            [:div.clearfix]
            [:div.table-responsive.table-full-width
             [:table
              {:class "table table-hover table-striped"}
              [c/thead 
               {:class "text-center"}
               ["Id" "Nome de usuário" "Jornada iniciada?" "Último checkin" "Mais"]]
              [:tbody
               (for [u (part-users-office-hours @current-page)]
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
                                         (select-keys u [:users/id]))
                         :rel "tooltip"}
                        "+"]]])]]]
            [c/pager (count part-users-office-hours) current-page]]))}]]))
          
(defmethod page-ui :admin.office-hours/list [_]
  [admin-page-ui "Jornada de trabalho" [office-hours-panel-ui]])

(defmethod page-ui :admin.office-hours.user/list [_]
  [admin-page-ui "Jornadas de trabalho" 
   [view-user-office-hours-panel-ui]])
