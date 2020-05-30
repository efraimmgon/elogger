(ns laconic-cms.apps.pages.views
  (:require
   laconic-cms.apps.pages.handlers
   [laconic-cms.utils.views :refer [default-base-ui]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]))

(defn edit-button [page current-user]
  (when (:users/admin @current-user)
    [:div.pull-right
     [:a.btn.btn-warning 
      {:href (rfe/href :admin.page/edit {:page/id (:page/id @page)})}
      "Edit"]]))

(defn default-page []
  (r/with-let [page (rf/subscribe [:pages/page])
               current-user (rf/subscribe [:identity])]
    (let [{:page/keys [id img-uri title subtitle content featured-img]} @page]
      [:div
       [:div.page-header.page-header-small
        ;; TODO: ADD background-image to pages schema
        {:style {:background-image (str "url('"
                                        (or (:file/data featured-img)
                                            "/main/img/bg3.jpg")
                                        "')")}}
        [:div.content-center
         [:div.container
          [:h1 title
           [edit-button page current-user]]
          (when subtitle
            [:h3 subtitle])]]]
       [:div.wrapper
        [:div.main
         [:div.section
          [:div.container
           content]]]]])))

(defn page-ui []
  [default-base-ui [default-page]])
