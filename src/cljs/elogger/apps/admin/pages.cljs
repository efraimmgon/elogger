(ns elogger.apps.admin.pages
  (:require
   [elogger.utils.user :refer [full-name]]
   [elogger.utils.events :refer [dispatch-n <sub]]
   [elogger.utils.forms :refer 
    [datetime-input-group input textarea radio-input]]
   [elogger.utils.components :as c :refer
    [card form-group thead]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))
   

; ------------------------------------------------------------------------------
; Components
; ------------------------------------------------------------------------------

(defn page-form-template [page]
  (let [current-user (rf/subscribe [:identity])
        path [:pages/page]]
    (fn []
      [:div
       [input {:type :hidden
               :name (conj path :page/author-id)
               :default-value (:users/id @current-user)}]
       [form-group
         "Title *"
         [input {:type :text
                 :name (conj path :page/title)
                 :class "form-control"}]]
       [form-group
         "Subtitle"
         [input {:type :text
                 :name (conj path :page/subtitle)
                 :class "form-control"}
          {:id :subtitle}]]
       [form-group
         "Status *"
         [radio-input
          {:name (conj path :page/status)
           :value "draft"
           :label " Draft"}]
         [radio-input
          {:name (conj path :page/status)
           :value "published"
           :label " Published"
           :default-checked true}]]
       [form-group
         "Content *"
         [textarea {:name (conj path :page/content)
                    :class "form-control"
                    :rows "7"}]]
       [form-group
         "Featured image"
         [input {:type :number
                 :name (conj path :page/featured-img-id)
                 :class "form-control"}]]
       ; TODO: tags
       [form-group
         "Published at"
         [datetime-input-group
          {:name (conj path :page/published-at)
           :id "page-published-at"}]]
       [form-group
         "Expires at"
         [datetime-input-group
          {:name (conj path :page/expires-at)
           :id "page-expires-at"}]]])))

(defn save-page-button [fields]
  [:div.pull-right
   [:button.btn.btn-primary
    {:on-click #(rf/dispatch 
                  [:pages/create-page 
                   {:doc @fields
                    :handler (fn [resp]
                               (rf/dispatch [:navigate! :admin.pages/list]))}])}
    "Save page"]])

(defn create-page-panel-ui []
  (r/with-let [fields (rf/subscribe [:pages/page])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "Create page"
        [save-page-button fields]]
       :content
       [:div
        [page-form-template fields]
        [save-page-button fields]]}]]))

(defn update-page-button [fields]
  [:div.pull-right
   [:button.btn.btn-primary
    {:on-click #(rf/dispatch 
                  [:pages/edit-page 
                   {:doc @fields
                    :handler (fn [resp]
                               (rf/dispatch [:navigate! :admin.pages/list]))}])}
                    
    "Update page"]])

(defn edit-page-panel-ui []
  (r/with-let [fields (rf/subscribe [:pages/page])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "Edit page"
        [update-page-button fields]]
       :content
       [:div
        [page-form-template fields]
        [update-page-button fields]]}]]))


(defn create-page-button []
  [:a.btn.btn-primary
   {:href (rfe/href :admin.page/create)}
   [:i.material-icons "add"]
   " New page"])

(defn pages-panel-ui []
  (r/with-let [pages (rf/subscribe [:pages/list])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "Pages"
        [:div.pull-right
         [create-page-button]]]
       :content
       (if-not (seq @pages)
          "No pages yet."
          [:div
           [:div.clearfix]
           [:div.table-responsive.table-full-width
            [:table.table.table-hover.table-striped
             [thead ["Page id" "Title" "Author" "View on site" "Delete"]]
             [:tbody
              (for [{:page/keys [id title author] :as page} @pages]
                ^{:key id}
                [:tr
                 [:td id]
                 [:td
                  [:a {:href (rfe/href :admin.page/edit {:page/id id})}
                   title]]
                 [:td (full-name author)]
                 [:td
                  [:a {:href (rfe/href :page/view {:page/id id})}
                   "View on site"]]
                 [:td
                  [:button.btn.btn-danger
                   {:on-click #(rf/dispatch 
                                 [:pages/delete-page 
                                   {:page/id id
                                    :handler (fn [resp] 
                                               (rf/dispatch [:pages/load]))}])}
                   [:i.material-icons "delete"]]]])]]]])}]]))

