(ns elogger.apps.blog.views.blog-posts
  (:require
   [elogger.utils.date :refer [to-date-str]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))

(defn newer-older-posts-buttons []
  [:div
   [:hr]
   [:div.row
    [:div.col-md-12
     [:div.pull-left
      [:button.btn.btn-link.btn-default.btn-move-left.btn-sm
       " "
       [:i.fa.fa-angle-left]
       " Older Posts"]]
     [:div.pull-right
      [:button.btn.btn-link.btn-default.btn-move-right.btn-sm
       "Newer Posts  "
       [:i.fa.fa-angle-right]]]]]])

(defn blog-post-tags2 [tags]
  (when (seq tags)
    (for [{:tag/keys [id title]} tags]
      ^{:key id}
      [:span.card-category.text-info title])))

(defn first-n-chars-only [blog-post-content n]
  (-> (apply str
         (take n blog-post-content))
      (str (if (> (count blog-post-content) n)
             "..." ""))))

(defn blog-card [blog-post]
  (let [{:blog-post/keys [id title content tags created-at featured-img]}
        blog-post
        blog-post-url (rfe/href :blog/view {:blog-post/id id})]
    [:div.card.card-plain.card-blog
     (when featured-img
       [:div.card-header.card-header-image
        [:a {:href blog-post-url}
         [:img.img.img-raised {:src (:file/data featured-img)}]]])
     [:div.card-body
      ;; Category/Tags
      [blog-post-tags2 tags]
      ;; Title
      [:h3.card-title
       [:a {:href blog-post-url} title]]
      ;; Created at
      [:h6.title-uppercase (to-date-str created-at)]
      ;; Body/Content
      [:h5.card-description
       (first-n-chars-only content 300)]
      ;; Read More
      [:a.btn.btn-primary.btn-round {:href blog-post-url}
       " Read More"]]]))

(defn blog-posts []
  (r/with-let [posts (rf/subscribe [:blog/posts])]
    [:div#blogs-4.blogs-4
     [:div.container
      [:div.row
       [:div.col-md-8.ml-auto.mr-auto
        [:h2.title "Latest Blogposts"]
        [:br]
        (if (empty? @posts)
          [:h3.text-center "No posts yet."]
          (for [{:blog-post/keys [id title content tags created-at
                                  featured-img]
                 :as blog-post} @posts]
            ^{:key id}
            [blog-card blog-post]))]]]]))

(defn jumbotron []
  [:div.page-header.header-filter.header-small
   {:style {:background-image "url('/main/img/bg3.jpg')"}
    :data-parallax "true"}
   [:div.container
    [:div.row
     [:div.col-md-8.ml-auto.mr-auto.text-center
      [:h2.title
       "A Place for Entrepreneurs to Share and Discover New Stories"]]]]])


(defn blog-posts-ui []
  [:div
   [jumbotron]
   [:div.main.main-raised
    [blog-posts]
    [newer-older-posts-buttons]]])
