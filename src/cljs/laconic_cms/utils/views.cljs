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
   [:a.nav-link
    attrs
    title]])

(defn dropdown-nav-item [title & items]
 [:li.dropdown.nav-item
  [:a.dropdown-toggle.nav-link
   {:data-toggle "dropdown"}
   title]
  (into [:div.dropdown-menu]
    items)])

(defn dnav-item [attrs title]
  [:a.dropdown-item attrs title])

(defn nav-menu [current-user]
  [:div.collapse.navbar-collapse
   [:ul.navbar-nav.ml-auto
    [nav-item {:href (rfe/href :home)} "Home"]
    [nav-item {:href (rfe/href :blog/list)} "Blog"]
    (when @current-user
      [dropdown-nav-item
       (:users/username @current-user)
       [dnav-item {:href (rfe/href :profile/view (select-keys @current-user [:users/id]))}
        "Profile"]
       (when (:users/admin @current-user)
         [dnav-item {:href (rfe/href :admin)}
          "Admin"])
       [dnav-item {:on-click #(dispatch-n [:auth/logout]
                                         [:navigate! :home])}
        "Logout"]])
    ;; LOGIN
    (when-not @current-user
      [nav-item {:on-click #(do (rf/dispatch-sync [:assoc-in [:auth/form] nil])
                                (rf/dispatch [:modal login-modal]))}
       "Login"])
    ;; REGISTER
    (when-not @current-user
      [nav-item {:on-click #(do (rf/dispatch-sync [:assoc-in [:auth/form] nil])
                                (rf/dispatch [:modal register-modal]))}
       "Register"])]])

(defn navbar []
  (r/with-let [current-user (rf/subscribe [:identity])]
    [:nav#sectionsNav.navbar.navbar-transparent.navbar-coloronscroll.fixed-top.navbar-expand-lg
     {:coloronscroll "100"}
     [:div.container
      [:div.navbar-translate
       [:a.navbar-brand
        {:href "/"}
        "Laconic CMS "]
       [:button.navbar-toggler
        {:aria-label "Toggle navigation",
         :aria-expanded "false",
         :data-toggle "collapse",
         :type "button"}
        [:span.sr-only "Toggle navigation"]
        [:span.navbar-toggler-icon]
        [:span.navbar-toggler-icon]
        [:span.navbar-toggler-icon]]]
      [nav-menu current-user]]]))

(defn footer []
  [:footer.footer.footer-default
   [:div.container
    [:nav.float-left
     [:ul
      [:li
       [:a
        {:href "https://www.creative-tim.com/"}
        "Laconic CMS"]]
      [:li
       [:a
        {:href "https://www.creative-tim.com/presentation"}
        "\n              About Us\n            "]]
      [:li
       [:a
        {:href "https://www.creative-tim.com/blog"}
        "\n              Blog\n            "]]
      [:li
       [:a
        {:href "https://www.creative-tim.com/license"}
        "\n              Licenses\n            "]]]]
    [:div.copyright.float-right
     (str "© " (.getFullYear (js/Date.))
          ", made with ")
     [:i.material-icons "favorite"]
     " by "
     [:a
      {:target "_blank", :href "https://github.com/efraimmgon/laconic-cms"}
      "Laconic CMS"]
     " for a better web."]]])

;;; Core

(defn base-ui [& forms]
  [with-deps
   {:deps (<sub [:main/deps])
    :loading [:div.loading "loading ..."]
    :loaded (into
             [:div.wrapper
              [modal-ui]
              [error-modal-ui]]
             forms)}])

(defn default-base-ui [body]
  [base-ui
   [navbar]
   body
   [footer]])
