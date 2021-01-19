(ns laconic-cms.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    cljs.pprint
    laconic-cms.apps.comments.handlers
    [laconic-cms.db :refer [default-db]]
    [laconic-cms.utils.deps :as deps]
    [laconic-cms.utils.events :refer [base-interceptors query]]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;; ---------------------------------------------------------------------------
;;; DISPATCHERS

(rf/reg-event-fx
 :ajax-error
 (fn [_ [_ response]]
   (js/console.log response)
   (let [message (get-in response [:response :message])
         error (with-out-str
                 (cljs.pprint/pprint
                   (:response response)))]
         ;status-text (:get-in response [:response :status-text])]
     {:dispatch [:set-error (or message error)]})))

(rf/reg-event-db
  :assoc-in
  base-interceptors
  (fn [db [path v]]
    (assoc-in db path v)))

(rf/reg-event-db
  :update-in
  base-interceptors
  (fn [db [path f & args]]
    (apply update-in db path f args)))

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-db
 :set-error
 (fn [db [_ error]]
   (assoc db :common/error error)))

(rf/reg-event-db
  :set-identity
  base-interceptors
  (fn [db [identity]]
    (assoc db :identity identity)))


(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-fx
  :add-deps!
  base-interceptors
  (fn [_ _]
    (let [dps [;;; Stylesheets
               {:dep/id "bootstrap.css"
                :dep/href "/css/bootstrap.min.css"}
               {:dep/id "gsdk.css"
                :dep/href "/css/gsdk.css"
                :dep/deps #{"bootstrap.css"}}
               {:dep/id "demo.css"
                :dep/href "/css/demo.css"
                :dep/deps #{"gsdk.css"}}
               {:dep/id "material-dashboard-css"
                :dep/href "/css/material-dashboard.css"}
               ;;; Fonts and icons
               {:dep/id "font-awesome.css"
                :dep/href "https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css"}
               {:dep/id "open-sans.css"
                :dep/href "https://fonts.googleapis.com/css?family=Grand+Hotel|Open+Sans:400,300"}
               {:dep/id "pe-icon-7-stroke.css"
                :dep/href "/css/pe-icon-7-stroke.css"}
               ;;; JS Core
               {:dep/id "jquery-ui.js"
                :dep/src "/js/jquery-ui.custom.min.js"}
               {:dep/id "bootstrap.js"
                :dep/src "/js/bootstrap.js"
                :dep/deps #{"jquery-ui.js"}}
               ;;; gsdk plugins
               {:dep/id "gsdk-checkbox.js"
                :dep/src "/js/gsdk-checkbox.js"}
               {:dep/id "gsdk-morphing.js"
                :dep/src "/js/gsdk-morphing.js"}
               {:dep/id "gsdk-radio.js"
                :dep/src "/js/gsdk-radio.js"}
               {:dep/id "gsdk-bootstrapswitch.js"
                :dep/src "/js/gsdk-bootstrapswitch.js"}
               ;;; other plugins
               {:dep/id "bootstrap-select.js"
                :dep/src "/js/bootstrap-select.js"}
               {:dep/id "bootstrap-datepicker.js"
                :dep/src "/js/bootstrap-datepicker.js"}
               {:dep/id "chartist.js"
                :dep/src "/js/chartist.min.js"}]]
      (deps/add-deps! dps))
    nil))


(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :init!
  base-interceptors
  (fn [{:keys [db]} _]
    {:dispatch-n [[:add-deps!]
                  ;; TODO: remove before deploy
                  [:auth/login (atom {:users/username "admin"
                                      :users/password "admin"})]]
     :db (merge db default-db)}))

(rf/reg-event-db
 :modal
 base-interceptors
 (fn [db [comp]]
   (let [modal-stack (:modal db)]
     (if (seq modal-stack)
       (update db :modal conj comp)
       (assoc db :modal [comp])))))

(rf/reg-event-db
 :remove-modal
 base-interceptors
 (fn [db _]
   (let [modal-stack (:modal db)]
     (if (seq modal-stack)
       (update db :modal pop)
       (assoc db :modal [])))))

;;; ---------------------------------------------------------------------------
;;; SUBSCRIPTIONS

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :modal
  (fn [db _]
    (let [modal-stack (:modal db)]
      (when (seq modal-stack)
        (peek modal-stack)))))

(rf/reg-sub
  :query
  (fn [db [_ path]]
     (get-in db path)))
       

(rf/reg-sub :main/deps query)
(rf/reg-sub :admin/deps query)
(rf/reg-sub :common/error query)
(rf/reg-sub :identity query)
