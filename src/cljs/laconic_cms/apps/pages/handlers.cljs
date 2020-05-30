(ns laconic-cms.apps.pages.handlers
  (:require
   [ajax.core :as ajax]
   [laconic-cms.utils.events :refer [query base-interceptors]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub trim-v debug]]))

; ------------------------------------------------------------------------------
; Utils
; ------------------------------------------------------------------------------

;; TODO: add :author as the :current-user
(defn page-defaults [page]
  (-> page
      (assoc :author "Efraim Augusto Gonçalves")
      (update :created-at #(or % (.toISOString (js/Date.))))))

(def ls-pages-key "laconic-cms-pages")

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
; SERVER CALLS

(reg-event-fx
 :pages/create-page
 base-interceptors
 (fn [_ [{:keys [doc handler]}]]
   ; TODO: validate, date to timestamp
   (ajax/POST "/api/pages"
              {:params doc
               :handler #(when handler (handler %))
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :pages/delete-page
 base-interceptors
 (fn [_ [{page-id :page/id handler :handler}]]
   (ajax/DELETE (str "/api/pages/" page-id)
                {:handler #(when handler (handler %))
                 :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :pages/edit-page
 base-interceptors
 (fn [_ [{:keys [doc handler]}]]
   ; TODO: validate, date to timestamp
   (ajax/PUT (str "/api/pages/" (:page/id doc))
             {:params (-> doc (dissoc :page/id))
              :handler #(when handler (handler %))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :pages/load
 base-interceptors
 (fn [_ _]
   (ajax/GET "/api/pages"
             {:handler #(dispatch [:pages/set-pages %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :pages/load-page
 base-interceptors
 (fn [_ [page-id]]
   ; ------- issue GET to backend, then set page
   (ajax/GET (str "/api/pages/" page-id)
             {:handler #(dispatch [:pages/set-page %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

; ------------------------------------------------------------------------------
; Misc

(reg-event-db
 :pages/set-page
 base-interceptors
 (fn [db [val]]
   (assoc-in db [:pages/page] val)))

(reg-event-db
 :pages/set-pages
 base-interceptors
 (fn [db [val]]
   (assoc-in db [:pages/list] val)))

; ------------------------------------------------------------------------------
; Subscriptions
; ------------------------------------------------------------------------------

(reg-sub :pages/list query)

(reg-sub :pages/page query)
