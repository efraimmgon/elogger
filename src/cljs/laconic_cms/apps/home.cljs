(ns laconic-cms.apps.home
  (:require
    [laconic-cms.apps.auth.views :refer [login-form register-form]]
    [laconic-cms.utils.components :refer [pretty-display form-group card]]
    [laconic-cms.utils.views :refer [default-base-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]))

; ------------------------------------------------------------------------------
; HOME PAGE BODY

; - When the user is logged in, his feed is his home screen
; - When the user is not logged in? Page with sign up form, or log in button

(defn jumbotron []
  [:div.parallax
   [:div.parallax-image
    ; TODO: save img assets
    [:img {:src "/img/home/about_5.jpg"}]]
   [:div.motto
    [:h1.border "Laconic CMS"]
    [:h3
     "A simple framework for your website."]]])

(defn feed-ui []
  [:div.section>div.container
    [card
     {:title "Feed"
      :content "A lot of text"}]])

(defn logged-out-ui []
  (r/with-let [path [:auth/form]
               fields (rf/subscribe path)]
    [:div.section.text-center>div.container
     [:div.row
      [:div.col-md-8.ml-auto.mr-auto
       [:h2.title "Don't have an account yet? Sign up!"]
       [card
        {:content [register-form path]
         :footer
         [:button.btn.btn-primary.btn-lg.btn-block
          {:on-click #(rf/dispatch [:auth/register fields])}
          "Register"]}]]]

     [:div.row
      [:div.col-md-8.ml-auto.mr-auto
       [:h2.title "Already a member? Log in!"]
       [card
        {:content [login-form path]
         :footer
         [:button.btn.btn-primary.btn-lg.btn-block
          {:on-click #(rf/dispatch [:auth/login fields])}
          "Login"]}]]]]))

  
(defn home-page []
  (r/with-let [identity (rf/subscribe [:identity])]
    [:div
     [jumbotron]
     [:div.main
       (if @identity
         [feed-ui]
         [logged-out-ui])]]))

(defn home-ui []
  [default-base-ui [home-page]])

