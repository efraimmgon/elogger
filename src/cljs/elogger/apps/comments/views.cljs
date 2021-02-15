(ns elogger.apps.comments.views
  (:require
    [elogger.utils.date :refer [to-date-str]]
    [elogger.utils.forms :refer [input textarea]]
    [elogger.utils.user :refer [full-name author? admin?]]
    [re-frame.core :as rf]
    [reagent.core :as r]
    [reitit.frontend.easy :as rfe]))

;;; ---------------------------------------------------------------------------
;;; REPLY

(defn reply-button [reply?-path]
  [:a.btn.btn-primary.btn-link;.float-right
   {:title "Reply to Comment",
    :rel "tooltip",
    :on-click #(rf/dispatch [:update-in reply?-path not])}
   [:i.material-icons "reply"]
   " Reply"])

(defn- form-group [input]
  [:div.form-group.label-floating.bmd-form-group
   input])

(defn reply-form* [path current-user blog-post ancestor]
  [:div
   ;; Author id
   [input {:type :hidden
           :name (conj path :threaded-comment/author-id)
           :default-value (:users/id @current-user)}]
   ;; Parent
   ;; NOTE: since our implementation is simplistic we only allow root comments
   ;; to be treated as parents. Thus, a reply to a nested comment will have
   ;; that comment's parent as its parent.
   [input {:type :hidden
           :name (conj path :threaded-comment/parent)
           :default-value (or (:threaded-comment/parent ancestor)
                              (:threaded-comment/id ancestor))}]
   ;; Blog post id
   [input {:type :hidden
           :name (conj path :blog-post/id)
           :default-value (:blog-post/id @blog-post)}]
   (when-not @current-user
     [form-group
      [input {:class "form-control"
              :type :text
              :placeholder "Name"
              :name (conj path :threaded-comment/author-name)}]])
   [form-group
    [input {:class "form-control"
            :type :text
            :placeholder "Title"
            :name (conj path :threaded-comment/title)}]]
   [form-group
    [textarea {:class "form-control"
               :rows "5"
               :placeholder " Write some nice stuff or nothing..."
               :name (conj path :threaded-comment/body)}]]])

(defn submit-comment-button [params]
  [:a.btn.btn-primary.btn-round.btn-wd.float-right
   {:on-click #(rf/dispatch [:blog.post.comment/create params])}
   "Post Comment"])

(defn cancel-button [comment-id]
  (let [reply? [:blog :post :comment comment-id :reply?]]
    [:a.btn.btn-danger.btn-round.btn-wd.float-right
     {:on-click #(rf/dispatch [:assoc-in reply? false])}
     "Cancel"]))

(defn reply-author [user]
  [:a.author.float-left 
   {:href (rfe/href :profile/view (select-keys @user [:users/id]))}
   [:div.avatar
    [:img.media-object
     {:src (or (get-in user [:users/avatar :files/data])
               "/admin/img/new_logo.png")
      :alt "64x64"}]]])

; author-name?
(defn reply-form [{:keys [ancestor]}]
  (r/with-let [current-user (rf/subscribe [:identity]),
               blog-post (rf/subscribe [:blog/post]),
               {:threaded-comment/keys [id]} ancestor,
               path [:blog :post :comment id :reply],
               fields (rf/subscribe [:query path])]
    [:div.media.media-post
     [reply-author current-user]
     [:div.media-body
      [reply-form* path current-user blog-post ancestor]
      [:div.media-footer
       [submit-comment-button {:doc fields, :path path}]
       (when (seq ancestor)
         [cancel-button id])]]]))


;;; ---------------------------------------------------------------------------
;;; COMMENT

(def comment-badges
  {:deleted [:span.badge.badge-danger "Deleted"]
   :unapproved [:span.badge.badge-warning "Pending approval"]})

(defn comment-status [c]
  (cond-> [:div]
          (:threaded-comment/is-deleted c) 
          (conj (:deleted comment-badges))
          
          (not (:threaded-comment/is-approved c))
          (conj (:unapproved comment-badges))))

(defn comment-style [c]
  (cond-> ""
          (:threaded-comment/is-deleted c)
          (str "rounded border border-danger")
          
          (not (:threaded-comment/is-approved c))
          (str "rounded border border-warning")))

(defn can-see-comment? [user c]
  (or (and (:threaded-comment/is-approved c) 
           (not (:threaded-comment/is-deleted c)))
      (author? (:threaded-comment/author c) user)
      (admin? user)))

(defn comment-author-status [commentable-author comment-author]
  (and (or (admin? comment-author)
           (author? commentable-author comment-author))
       [:span.badge.badge-success "mod"]))

(defn liked-by? [user likes]
  (some #(when (= (:users/id user)
                  (:likes/user-id %))
           (:likes/id %))
        likes))

(defn comment-likes-template [c]
  (let [current-user (rf/subscribe [:identity])]
    (fn [c]
      (let [{:threaded-comment/keys [id likes]} c
            liked? (r/atom (liked-by? @current-user likes))
            like-count (r/atom (count likes))
            user-id (:users/id @current-user)]
        [:a.btn.btn-link.float-left
          {:class (when @liked? "btn-danger")
           :on-click #(when user-id
                        (if @liked?
                          (rf/dispatch [:blog.post.comment/unlike id @liked?])
                          (rf/dispatch [:blog.post.comment/like id user-id])))}
          [:i.material-icons "favorite"] " "
          @like-count]))))




(defn comment-ui
  "Renders a root comment. If it has children, renders them inside
  its body."
  [{:threaded-comment/keys 
    [id body author author-name created-at
     parent author-name is-approved is-deleted likes]
    :as root}
   & [children]]
  (r/with-let [current-user (rf/subscribe [:identity])]
    ;; A deleted, or unapproved post is only shown to the admin or author.
    (when (can-see-comment? @current-user root)
      (let [reply?-path [:blog :post :comment id :reply?]
            reply? (rf/subscribe [:query reply?-path])]
        [:div.media
         ;;; Comment's author info
         (when-let [avatar (:users/avatar author)])
         [:a.float-left {:href (str "/users/" (:users/id author))}
          [:div.avatar
           [:img.media-object
            {:alt "...",
             :src (or (get-in author [:users/avatar :files/data]
                       "/admin/img/new_logo.png"))}]]]
         [:div.media-body
          [:h4.media-heading
           (if author
             [:a {:href (rfe/href :profile/view (select-keys author [:users/id]))}
              (full-name author)]
             author-name)
           " "
           [:small (to-date-str created-at)]
           " "
           [comment-status root]
           " "
           [comment-author-status (:blog-post/author @(rf/subscribe [:blog/post]))
                                  author]]
          ;; Comment body
          [:p body]
          ;; Can only reply if the comment is not nested.
          [:div.media-footer
           (if @reply?
             [reply-form {:ancestor root}]
             [reply-button reply?-path])

           [comment-likes-template root]]
          ;; Children
          (when (seq children)
            (for [child children]
              ^{:key (:threaded-comment/id child)}
              [comment-ui child]))]]))))
