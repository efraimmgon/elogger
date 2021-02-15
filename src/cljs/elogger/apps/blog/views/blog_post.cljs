(ns elogger.apps.blog.views.blog-post
  (:require
    [elogger.apps.comments.views :refer [comment-ui reply-form]]
    [elogger.utils.components :refer [pretty-display]]
    [elogger.utils.user :refer [full-name]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

;;; ---------------------------------------------------------------------------
;;; Misc

(defn blog-post-images []
  [:div.section.col-md-10.ml-auto.mr-auto
   [:div.row
    [:div.col-md-4
     [:img.img-raised.rounded.img-fluid
      {:src "/main/img/examples/studio-1.jpg",
       :alt "Raised Image"}]]
    [:div.col-md-4
     [:img.img-raised.rounded.img-fluid
      {:src "/main/img/examples/studio-2.jpg",
       :alt "Raised Image"}]]
    [:div.col-md-4
     [:img.img-raised.rounded.img-fluid
      {:src "/main/img/examples/studio-3.jpg",
       :alt "Raised Image"}]]]])

(defn social-media-feedback [blog-post]
  [:div.col-md-6
   [:a.btn.btn-google.btn-round.float-right
    {:href "#pablo"}
    [:i.fa.fa-google]
    " 232\n\t\t\t\t\t\t\t"]
   [:a.btn.btn-twitter.btn-round.float-right
    {:href "#pablo"}
    [:i.fa.fa-twitter]
    " 910\n\t\t\t\t\t\t\t"]
   [:a.btn.btn-facebook.btn-round.float-right
    {:href "#pablo"}
    [:i.fa.fa-facebook-square]
    " 872\n\t\t\t\t\t\t\t"]])

(defn blog-post-tags [blog-post]
  [:div.col-md-6
   [:div.blog-tags
    (if (empty? (:blog-post/tags @blog-post))
      "No tags."
      [:div
       "Tags:"
       (for [{:tag/keys [title id]} (:blog-post/tags @blog-post)]
         ^{:key id}
         [:span.badge.badge-primary.badge-pill title])])]])

(defn blog-post-author [blog-post]
  (let [{:users/keys [avatar id bio] :as author}
        (:blog-post/author @blog-post)]
    [:div.card.card-profile.card-plain
     [:div.row
      [:div.col-md-2
       ;; TODO: avatar
       [:div.card-avatar
        [:a {:href (rfe/href :profile/view {:users/id id})}
         [:img.img
          {:src (or (:files/data avatar)
                    "/admin/img/new_logo.png")}]]
        [:div.ripple-container]]]
      [:div.col-md-8
       [:h4.card-title
        [:a {:href (rfe/href :profile/view {:users/id id})}
         (full-name author)]]
       [:p.description bio]]
      ;; TODO: follow posts (probably by emailing)
      (comment
        [:div.col-md-2
         [:button.btn.btn-default.pull-right.btn-round
          {:type "button"}
          "Follow"]])]]))


;;; ---------------------------------------------------------------------------
;;; COMMENTS


(defn blog-post-comments []
  (r/with-let [comments (rf/subscribe [:blog.post/comments])
               comments-count (rf/subscribe [:blog.post.comments.visible/count])
               f (rf/subscribe [:query [:blog.post :comment]])]
    [:div.section.section-comments
     [:div.row
      [:div.col-md-8.ml-auto.mr-auto
       (if (empty? @comments)
         [:div.media-area
          [:h3.title.text-center "No comments yet."]]
         ;; COMMENTS
         [:div.media-area
          ;; COMMENTS COUNT
          [:h3.title.text-center @comments-count " Comments"]
          (doall
            (for [root (->> @comments
                             (filter (comp not :threaded-comment/parent)))
                  :let [children (->> @comments
                                      (filter #(= (:threaded-comment/parent %)
                                                  (:threaded-comment/id root))))]]
              ^{:key (:threaded-comment/id root)}
              [comment-ui root children]))])
       ;; TODO: Code to add new comment
       [:h3.title.text-center "Post your comment"]
       [reply-form nil]]]]))

; ------------------------------------------------------------------------------
; Blog Post

(defn blog-post-body [blog-post]
  (let [{:blog-post/keys [content]} @blog-post]
    [:div.main.main-raised
     [:div.container
      [:div.section.section-text
       [:div.row
        [:div.col-md-8.ml-auto.mr-auto
         [:div#content
          content]]]]
        ;[blog-post-images]]]
      [:div.section.section-blog-info
       [:div.row
        [:div.col-md-8.ml-auto.mr-auto
         [:div.row
          [blog-post-tags blog-post]
          [social-media-feedback blog-post]]
         [:hr]
         [blog-post-author blog-post]]]]
      [blog-post-comments]]]))


(defn jumbotron [blog-post]
  (let [{:blog-post/keys [id title subtitle featured-img]} @blog-post]
    [:div.page-header.header-filter
     ;; TODO: replace by featured-img:
     {:style {:background-image "url('/main/img/bg7.jpg')"}
      :data-parallax "true"}
     [:div.container
      [:div.row
       [:div.col-md-8.ml-auto.mr-auto.text-center
        [:h1.title title]
        (when subtitle
          [:h4 subtitle])
        [:br]
        [:a.btn.btn-rose.btn-round.btn-lg
         {:href (str (rfe/href :blog/view {:blog-post/id id}) "#content")}
         [:i.material-icons "format_align_left"]
         " Read Article"]]]]]))

(defn blog-post-ui []
  (r/with-let [post (rf/subscribe [:blog/post])]
    [:div.blog-post
     [jumbotron post]
     [blog-post-body post]]))
