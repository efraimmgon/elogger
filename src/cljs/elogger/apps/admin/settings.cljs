(ns elogger.apps.admin.settings
  (:require
    [elogger.apps.admin.utils :refer [admin-page-ui]]
    [elogger.utils.components :as c]
    [elogger.utils.forms :refer [input]]
    [elogger.utils.views :refer [page-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(defn settings-form [settings]
  (r/with-let [path [:admin/settings]]
    [:div
     [:fieldset
      [:legend "Dados do escritório"]
      [c/form-group
       "Nome"
       [input {:type :text
               :name (conj path :office/title)
               :class "form-control"}]]
      [c/form-group
       "Descrição"
       [input {:type :text
               :name (conj path :office/description)
               :class "form-control"}]]]
     [:fieldset
      [:legend "Localização do escritório para checkin"]
      [c/form-group
       "Endereço"
       [input {:type :text
               :name (conj path :office/address)
               :class "form-control"}]]
      [c/form-group
       "Latitude"
       [input {:type :number
               :name (conj path :office/latitude)
               :class "form-control"}]]
      [c/form-group
       "Longitude"
       [input {:type :number
               :name (conj path :office/longitude)
               :class "form-control"}]]]]))
    

(defn settings-panel-ui []
  (r/with-let [settings (rf/subscribe [:admin/settings])]
    [:div.row>div.col-md-12
     [c/card
      {:header [:h4 "Configurações"]
       :body 
       [settings-form settings]
       :footer
       [:button.btn.btn-primary
        {:on-click #(rf/dispatch
                      [:admin.settings/update settings])}
        "Atualizar"]}]]))
       

(defmethod page-ui :admin/settings [_]
  [admin-page-ui "Configurações" [settings-panel-ui]])