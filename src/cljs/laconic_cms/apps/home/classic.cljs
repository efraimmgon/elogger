(ns laconic-cms.apps.home.classic
  (:require
    [laconic-cms.utils.components :refer [pretty-display]]
    [laconic-cms.utils.views :refer [default-base-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]))

; ------------------------------------------------------------------------------
; HOME PAGE BODY

(defn jumbotron []
  [:div.page-header.header-filter
   {:style {:background-image "url('/main/img/profile_city.jpg')"}
    :data-parallax "true"}
   [:div.container
    [:div.row
     [:div.col-md-6
      [:h1.title "Your Story Starts With Us."]
      [:h4 "Every landing page needs a small description after the big bold title, that's why we added this text here. Add here all the information that can make you or your product create the first impression."]
      [:br]
      [:a.btn.btn-danger.btn-raised.btn-lg
       {:target "_blank",
        :href "https://www.youtube.com/watch?v=dQw4w9WgXcQ"}
       [:i.fa.fa-play]
       " Watch video"]]]]])

(defn section-1-products []
  [:div.section.text-center
   [:div.row
    [:div.col-md-8.ml-auto.mr-auto
     [:h2.title "Let's talk product"]
     [:h5.description
      "This is the paragraph where you can write more details about your product. Keep you user engaged by providing meaningful information. Remember that by this time, the user is curious, otherwise he wouldn't scroll to get here. Add a button if you want the user to see more."]]]
   [:div.features
    [:div.row
     [:div.col-md-4
      [:div.info
       [:div.icon.icon-info [:i.material-icons "chat"]]
       [:h4.info-title "Free Chat"]
       [:p
        "Divide details about your product or agency work into parts. Write a few lines about each one. A paragraph describing a feature will be enough."]]]
     [:div.col-md-4
      [:div.info
       [:div.icon.icon-success [:i.material-icons "verified_user"]]
       [:h4.info-title "Verified Users"]
       [:p
        "Divide details about your product or agency work into parts. Write a few lines about each one. A paragraph describing a feature will be enough."]]]
     [:div.col-md-4
      [:div.info
       [:div.icon.icon-danger [:i.material-icons "fingerprint"]]
       [:h4.info-title "Fingerprint"]
       [:p
        "Divide details about your product or agency work into parts. Write a few lines about each one. A paragraph describing a feature will be enough."]]]]]])

(defn section-2-team []
  [:div.section.text-center
   [:h2.title "Here is our team"]
   [:div.team
    [:div.row
     [:div.col-md-4
      [:div.team-player
       [:div.card.card-plain
        [:div.col-md-6.ml-auto.mr-auto
         [:img.img-raised.rounded-circle.img-fluid
          {:alt "Thumbnail Image",
           :src "/main/img/faces/avatar.jpg"}]]
        [:h4.card-title
         "Gigi Hadid\n                    "
         [:br]
         [:small.card-description.text-muted "Model"]]
        [:div.card-body
         [:p.card-description
          "You can write here details about one of your team members. You can give more details about what they do. Feel free to add some\n                      "
          [:a {:href "#"} "links"]
          " for people to be able to follow them outside the site."]]
        [:div.card-footer.justify-content-center
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-twitter]]
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-instagram]]
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-facebook-square]]]]]]
     [:div.col-md-4
      [:div.team-player
       [:div.card.card-plain
        [:div.col-md-6.ml-auto.mr-auto
         [:img.img-raised.rounded-circle.img-fluid
          {:alt "Thumbnail Image",
           :src "/main/img/faces/christian.jpg"}]]
        [:h4.card-title
         "Christian Louboutin"
         [:br]
         [:small.card-description.text-muted "Designer"]]
        [:div.card-body
         [:p.card-description
          "You can write here details about one of your team members. You can give more details about what they do. Feel free to add some\n                      "
          [:a {:href "#"} "links"]
          " for people to be able to follow them outside the site."]]
        [:div.card-footer.justify-content-center
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-twitter]]
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-linkedin]]]]]]
     [:div.col-md-4
      [:div.team-player
       [:div.card.card-plain
        [:div.col-md-6.ml-auto.mr-auto
         [:img.img-raised.rounded-circle.img-fluid
          {:alt "Thumbnail Image",
           :src "/main/img/faces/kendall.jpg"}]]
        [:h4.card-title
         "Kendall Jenner"
         [:br]
         [:small.card-description.text-muted "Model"]]
        [:div.card-body
         [:p.card-description
          "You can write here details about one of your team members. You can give more details about what they do. Feel free to add some\n                      "
          [:a {:href "#"} "links"]
          " for people to be able to follow them outside the site."]]
        [:div.card-footer.justify-content-center
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-twitter]]
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-instagram]]
         [:a.btn.btn-link.btn-just-icon
          {:href "#pablo"}
          [:i.fa.fa-facebook-square]]]]]]]]])

(defn section-3-contact []
  [:div.section.section-contacts
   [:div.row
    [:div.col-md-8.ml-auto.mr-auto
     [:h2.text-center.title "Work with us"]
     [:h4.text-center.description
      "Divide details about your product or agency work into parts. Write a few lines about each one and contact us about any further collaboration. We will responde get back to you in a couple of hours."]
     [:form.contact-form
      [:div.row
       [:div.col-md-6
        [:div.form-group
         [:label.bmd-label-floating "Your Name"]
         [:input.form-control {:type "email"}]]]
       [:div.col-md-6
        [:div.form-group
         [:label.bmd-label-floating "Your Email"]
         [:input.form-control {:type "email"}]]]]
      [:div.form-group
       [:label.bmd-label-floating
        {:for "exampleMessage"}
        "Your Message"]
       [:textarea#exampleMessage.form-control
        {:rows "4", :type "email"}]]
      [:div.row
       [:div.col-md-4.ml-auto.mr-auto.text-center
        [:button.btn.btn-primary.btn-raised
         "Send Message"]]]]]]])

(defn home-page []
  [:div
   [jumbotron]
   [:div.main.main-raised
    [:div.container
     [section-1-products]
     [section-2-team]
     [section-3-contact]]]])

(defn home-ui []
  [default-base-ui [home-page]])
