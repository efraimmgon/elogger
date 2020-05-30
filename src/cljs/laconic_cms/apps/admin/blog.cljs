(ns laconic-cms.apps.admin.blog
  (:require
   [laconic-cms.utils.components :as c :refer
    [card thead form-group]]
   [laconic-cms.utils.forms :refer 
    [input textarea datetime-input-group radio-input]]
   [laconic-cms.utils.user :refer [full-name]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))



; ------------------------------------------------------------------------------
; Components
; ------------------------------------------------------------------------------

(defn post-form-template [fields]
  (let [current-user (rf/subscribe [:identity])
        path [:blog/post]]
    (when (nil? @fields)
      (rf/dispatch [:assoc-in (conj path :blog-post/author-id) (:users/id @current-user)]))
    (fn []
      [:div
       [form-group
         "Title *"
         [input {:type :text
                 :name (conj path :blog-post/title)
                 :class "form-control"}]]
       [form-group
         "Subtitle"
         [input {:type :text
                 :name (conj path :blog-post/subtitle)
                 :class "form-control"}]]
       ;; Default selected?
       [form-group
         "Status *"
         [radio-input {:name (conj path :blog-post/status)
                       :value "draft"
                       :label "Draft"}]
         [radio-input {:name (conj path :blog-post/status)
                       :value "published"
                       :label "Published"
                       :default-checked true}]]
       [form-group
         "Content *"
         [textarea {:name (conj path :blog-post/content)
                    :class "form-control"
                    :rows "7"}]]
       [form-group
         "Featured image [TODO]"
         [input {:type :number
                 :name (conj path :blog-post/featured-img-id)
                 :class "form-control"}]]
       ;; TODO: tags
         ; (when (seq @tags)
         ;   [form-group
         ;     "Tags [TODO]"
         ;     (for [{:keys [id title]} @tags]
         ;       ^{:key id}
         ;       [:label.form-check-label
         ;        [input {:type :checkbox
         ;                :name (conj path :blog-post/tag id)
         ;                :class "form-check-input"}]
         ;        " " title])]))
       ; end TODO
       [form-group
         "Published at"
         [datetime-input-group
          {:name (conj path :blog-post/published-at)}]]
       [form-group
         "Expires at"
         [datetime-input-group
          {:name (conj path :blog-post/expires-at)}]]])))

(defn save-post-button [fields]
  [:div.pull-right
    [:button.btn.btn-primary
     {:on-click #(rf/dispatch 
                   [:blog/create-post 
                    {:doc @fields
                     :handler (fn [resp]
                                (rf/dispatch
                                  [:navigate! :admin.posts/list]))}])}
     "Save post"]])

;; NOTE: when get tags, massage them to the format expected by doc.
(defn create-post-panel-ui []
  (r/with-let [fields (rf/subscribe [:blog/post])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "New post"
        [save-post-button fields]]
       :content
       [:div
        [post-form-template fields]
        [save-post-button fields]]}]]))

(defn update-post-button [fields]
  [:div.pull-right
    [:button.btn.btn-primary
     {:on-click #(rf/dispatch [:blog/edit-post 
                               {:doc @fields
                                :handler (fn [resp]
                                           (rf/dispatch 
                                             [:navigate! :admin.posts/list]))}])}
     "Update post"]])

;; NOTE: when get tags, massage them to the format expected by doc.
(defn edit-post-panel-ui []
  (r/with-let [fields (rf/subscribe [:blog/post])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "Edit post"
        [update-post-button fields]]
       :content
       [:div
        [post-form-template fields]
        [update-post-button fields]]}]]))

(defn create-post-button []
  [:a.btn.btn-primary
   {:href (rfe/href :admin.post/create)}
   [:i.material-icons "add"]
   " Create post"])

(defn posts-panel-ui []
  (r/with-let [posts (rf/subscribe [:blog/posts])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div
        "Blog Posts"
        [:div.pull-right
         [create-post-button]]]
       :content
       (if-not (seq @posts)
          "No posts yet."
          [:div
           [:div.clearfix]
           [:div.table-responsive.table-full-width
            [:table.table.table-hover.table-striped
             [thead ["Post id" "Title" "Author" "View on site" "Delete"]]
             [:tbody
              (for [{:blog-post/keys [id author title] :as post} @posts]
                ^{:key id}
                [:tr
                 ;; Post id
                 [:td id]
                 ;; Title
                 [:td
                  [:a {:href (rfe/href :admin.post/edit {:blog-post/id id})}
                   title]]
                 ;; Author
                 [:td (full-name (:blog-post/author post))]
                 ;; View on site
                 [:td
                  [:a {:href (rfe/href :blog/view {:blog-post/id id})}
                   "View on site"]]
                 ;; Delete
                 [:td
                  [:button.btn.btn-danger
                   {:on-click #(rf/dispatch 
                                 [:blog/delete-post 
                                  {:blog-post/id id
                                   :handler (fn [resp]
                                              (rf/dispatch
                                                [:blog.posts/load]))}])}
                   [:i.material-icons "delete"]]]])]]]])}]]))
