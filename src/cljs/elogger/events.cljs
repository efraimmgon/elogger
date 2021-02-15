(ns elogger.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    cljs.pprint
    elogger.apps.comments.handlers
    [elogger.db :refer [default-db]]
    [elogger.utils.deps :as deps]
    [elogger.utils.events :refer [base-interceptors query]]
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

(def main-deps
  [{:dep/id "material-kit-css"
    :dep/href "/css/material-kit.css"}
   {:dep/id "demo-css"
    :dep/href "/css/demo.css"}
   {:dep/id "material-kit-js"
    :dep/src "/js/material-kit.js"}])

(def admin-deps
  [{:dep/id "material-dashboard-css"
    :dep/href "/css/material-dashboard.css"}
   {:dep/id "perfect-scrollbar-js"
    :dep/src "/js/plugins/perfect-scrollbar.jquery.min.js"}
   {:dep/id "sweetalert2-js"
    :dep/src "/js/plugins/sweetalert2.js"}
   {:dep/id "jasny-bootstrap-js"
    :dep/src "/js/plugins/jasny-bootstrap.min.js"}
   {:dep/id "chartist-js"
    :dep/src "/js/plugins/chartist.min.js"}
   {:dep/id "material-dashboard-js"
    :dep/src "/js/material-dashboard.js"}])

(rf/reg-event-fx
  :add-deps!
  base-interceptors
  (fn [_ _]
    (deps/add-deps! (into main-deps admin-deps))
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
