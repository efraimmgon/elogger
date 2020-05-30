(ns laconic-cms.utils.views
  (:require
    [laconic-cms.apps.auth.views :refer [login-modal register-modal]]
    [laconic-cms.utils.components :as c]
    [laconic-cms.utils.deps :refer [with-deps]]
    [laconic-cms.utils.events :refer [<sub dispatch-n]]
    [re-frame.core :as rf]
    [reagent.core :as r]
    [reitit.frontend.easy :as rfe]))

(defn modal-ui []
  (when-let [modal-comp (<sub [:modal])]
    [modal-comp]))

(defn error-modal-ui []
  (when-let [error (<sub [:common/error])]
    [c/modal
     {:header
      "An error has occured"
      :body
       [:div;.alert.bg-danger
        [:pre error]]
      :footer
       [:div
        [:button.btn.btn-sm.btn-danger
         {:on-click #(rf/dispatch [:set-error nil])}
         "OK"]]}]))

(defn nav-item [attrs title]
  [:li.nav-item
   [:a.nav-link attrs
    title]])

(defn nav-menu [current-user]
  [:ul.navbar-nav.ml-auto
   [nav-item {:href (rfe/href :home)} "Home"]
   [nav-item {:href (rfe/href :blog/list)} "Blog"]
   (when @current-user
     [:li.nav-item.dropdown
      [:a.nav-link.dropdown-toggle
       {:data-toggle "dropdown"}
       (:users/username @current-user)]
      [:ul.dropdown-menu.dropdown-menu-right
       ;; Profile link
       [:a.dropdown-item 
        {:href (rfe/href :profile/view (select-keys @current-user [:users/id]))}
        "Profile"]
       ; TODO: admin router 
       (when (:users/admin @current-user)
         [:a.dropdown-item 
          {:href (rfe/href :admin)}
          "Admin"])
       [:a.dropdown-item {:on-click #(dispatch-n [:auth/logout] 
                                                 [:navigate! :home])}
        "Logout"]]])
   ;; LOGIN
   (when-not @current-user
     [nav-item {:on-click #(do (rf/dispatch-sync [:assoc-in [:auth/form] nil])
                               (rf/dispatch [:modal login-modal]))}
      "Login"])
   ;; REGISTER
   (when-not @current-user
     [nav-item {:on-click #(do (rf/dispatch-sync [:assoc-in [:auth/form] nil])
                               (rf/dispatch [:modal register-modal]))}
      "Register"])])

(defn navbar []
  (r/with-let [current-user (rf/subscribe [:identity])]
    [:nav.navbar.navbar-expand-md.fixed-top
      [:div.container
       [:div.navbar-translate
        [:a.navbar-brand
         {:href "/"}
         "Laconic CMS"]
        [:button.navbar-toggler
         {:aria-label "Toggle navigation",
          :aria-expanded "false",
          :data-toggle "collapse",
          :type "button"}
         [:span.sr-only "Toggle navigation"]
         [:span.navbar-toggler-icon]
         [:span.navbar-toggler-icon]
         [:span.navbar-toggler-icon]]]
       [:div.collapse.navbar-collapse
        [nav-menu current-user]]]]))

(defn footer []
  [:footer.footer.footer-default
   [:div.container
    [:nav.float-left
     [:ul
      [:li>a {:href (rfe/href :home)}
        "Laconic CMS"]
      [:li>a {:href "/about-us"}
        "About US"]
      [:li>a {:href "/blog"}
        "Blog"]]]
    [:div.copyright.float-right
     "© "
     (.getFullYear (js/Date.))
     ", made with "
     [:i.material-icons "favorite"]
     " by "
     [:a
      {:target "_blank", :href "https://www.creative-tim.com"}
      "Creative Tim"]
     " for a better web."]]])

(defn base-ui [& forms]
  [with-deps
   {:deps (<sub [:main/deps])
    :loading [:div.loading "loading ..."]
    :loaded (into
             [:div
              [modal-ui]
              [error-modal-ui]]
             forms)}])

(defn default-base-ui [body]
  [base-ui
   [navbar]
   body
   [footer]])
