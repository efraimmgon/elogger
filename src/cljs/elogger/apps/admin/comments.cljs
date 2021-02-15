(ns elogger.apps.admin.comments
  (:require
   [elogger.utils.components :refer
     [form-group card thead tbody]]
   [elogger.utils.forms :refer 
    [input textarea radio-input datetime-input-group]]
   [elogger.utils.user :refer [full-name author?]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))

(defn bool-icon [x]
  (if (boolean x)
    [:i.material-icons "check"]
    [:i.material-icons "close"]))


(defn comment-form [doc]
  (let [current-user (rf/subscribe [:identity])
        path [:comments/comment]]
    (fn []
      [:div
       [form-group
        "Comment id"
        [input {:type :text
                :name (conj path :threaded-comment/id)
                :class "form-control"
                :disabled true}]]
       [form-group
        "Author"
        (if-let [author (get-in @doc [:threaded-comment/author :users/username])]
          [:input {:type :text
                   :value author
                   :class "form-control"
                   :disabled true}]
          [:input {:type :text
                   :class "form-control"
                   :value "Anonymous"
                   :disabled true}])]
       [form-group
        "Title"
        [input {:type :text
                :name (conj path :threaded-comment/title)
                :class "form-control"
                :disabled (not (author? @doc @current-user))}]]
       [form-group
        "Body"
        [textarea {:name (conj path :threaded-comment/body)
                   :class "form-control"
                   :disabled (not (author? @doc @current-user))
                   :rows "7"}]]
       [form-group
        "Approved?"
        [radio-input
         {:name (conj path :threaded-comment/is-approved)
          :label "Yes"
          :value true}]
        [radio-input
         {:name (conj path :threaded-comment/is-approved)
          :label "No"
          :value false}]]
       [form-group
        "Deleted?"
        [radio-input
         {:name (conj path :threaded-comment/is-deleted)
          :label "Yes"
          :value true}]
        [radio-input
         {:name (conj path :threaded-comment/is-deleted)
          :label "No"
          :value false}]]
       [form-group
        "Created at"
        [datetime-input-group
         {:name (conj path :threaded-comment/created-at)
          :disabled true}]]])))

(defn delete-comment-button [doc]
  [:button.btn.btn-danger
   {:on-click #(rf/dispatch 
                 [:comments/delete-comment
                  {:threaded-comment/id (:threaded-comment/id @doc)
                   :handler (fn [resp]
                              (rf/dispatch
                                [:blog-posts.comments/load-comments]))}])}
   [:i.material-icons "delete"]
   " Delete"])

(defn save-comment-button [doc]
  [:div.pull-right
   [delete-comment-button doc]
   [:a.btn.btn-primary
    {:on-click #(rf/dispatch 
                  [:comments/update-comment 
                   {:doc @doc
                    :handler (fn [resp]
                               (rf/dispatch
                                 [:navigate! :admin.comments/list]))}])}
    [:i.material-icons "save"]
    " Update"]])

(defn edit-comment-ui []
  (r/with-let [doc (rf/subscribe [:comments/comment])]
    [:div.row>div.col-md-12
     [card {:title 
            [:div "Edit comment"
             [save-comment-button doc]]

            :content
            [:div
             [comment-form doc]
             [save-comment-button doc]]}]]))

(defn comments-tbody [all-comments]
  [tbody
   (for [{:blog-post/keys [id comments]} @all-comments
         {:threaded-comment/keys
          [author body created-at is-approved is-deleted]
          :as tcomment} comments]
     ^{:keys (:threaded-comment/id tcomment)}
     [;; Author:
      (if (seq author)
        [:a {:href (rfe/href :profile/view (select-keys author [:users/id]))}
         (full-name author)]
        "anonymous"),
      ;; Body/Edit:
      [:a {:href (rfe/href :admin.comment/edit 
                           (select-keys tcomment [:threaded-comment/id]))}
       (let [len (count body)
             bd (->> body (take 200) (apply str))]
         (if (> len 200)
           (str bd "...")
           bd))]
       
      ;; Created at:
      created-at
      ;; Approved?
      [bool-icon is-approved]
      ;; Deleted?
      [bool-icon is-deleted]
      ;; View on site:
      [:a {:href (rfe/href :blog/view {:blog-post/id id})}
       "View on site"]])])


(defn comments-ui []
  (r/with-let [all-comments (rf/subscribe [:comments/list])]
    [:div.row>div.col-md-12
     [card
      {:title
       [:div "Comments"]
       :content
       [:div
        (if-not (seq @all-comments)
           [:p "No comments yet."]
           [:div
            [:div.clearfix]
            [:div.table-responsive.table-full-width.text-center
             [:table.table.table-hover.table-striped
              [thead ["Author" "Body" "Created at" "Approved?" "Deleted?" ""]]
              [comments-tbody all-comments]]]])]}]]))
