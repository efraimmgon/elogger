(ns elogger.apps.admin.handlers
  (:require
   [ajax.core :as ajax]
   [elogger.utils.events :refer [query base-interceptors]]
   [re-frame.core :as rf]))

; ------------------------------------------------------------------------------
; Handlers
; ------------------------------------------------------------------------------

(rf/reg-event-fx
  :admin/load-settings
  base-interceptors
  (fn [_ _]
    (ajax/GET "/api/admin/settings"
              {:handler #(rf/dispatch [:assoc-in [:admin/settings] %])
               :error-handler #(rf/dispatch [:set-error %])
               :response-format :json
               :keywords? true})
    nil))

(rf/reg-event-fx
  :admin.settings/update
  base-interceptors
  (fn [_ [settings]]
    (ajax/PUT "/api/admin/settings"
              {:params @settings
               :handler #()
               :error-handler #(rf/dispatch [:ajax-error %])})
    nil))


; ------------------------------------------------------------------------------
; SUBS
; ------------------------------------------------------------------------------

(rf/reg-sub :admin/settings query)