(ns elogger.apps.admin.utils
  (:require
    [elogger.utils.deps :refer [with-deps]]
    [elogger.utils.events :refer [<sub]]
    [elogger.utils.views :refer [modal-ui error-modal-ui page-ui]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

;;; ---------------------------------------------------------------------------
;;; Views
;;; ---------------------------------------------------------------------------

;;; ---------------------------------------------------------------------------
;;; Sidebar

(defn sidebar-item-ui [href icon title]
  [:li.nav-item
   [:a.nav-link {:href href}
    icon
    [:p title]]])

(defn sidebar-ui []
  [:div.sidebar
   {:data-image "/admin/img/sidebar-1.jpg",
    :data-background-color "white",
    :data-color "purple"}
   ; "<!-- Tip 1: You can change the color of the sidebar using: data-color=\"purple | azure | green | orange | danger\"
   ;     Tip 2: you can also add an image using data-image tag -->"
   [:div.logo
    [:a.simple-text.logo-normal
     {:href "#/"}
     "Elogger"]]
   [:div.sidebar-wrapper
    [:ul.nav
     [sidebar-item-ui
      (rfe/href :admin) [:i.material-icons "dashboard"] "Painel"]
     ; [sidebar-item-ui
     ;  (rfe/href :admin.pages/list) [:i.material-icons "library_books"] "Pages"]
     ; [sidebar-item-ui
     ;  (rfe/href :admin.posts/list) [:i.material-icons "assignment"] "Blog Posts"]
     [sidebar-item-ui
      (rfe/href :admin.users/list) [:i.material-icons "person"] "Usuários"]
     [sidebar-item-ui
      (rfe/href :admin.office-hours/list) 
      [:i.material-icons "switch_account"] 
      "Jornada de trabalho"]
     [sidebar-item-ui
      (rfe/href :admin/settings)
      [:i.material-icons "settings"]
      "Configurações"]]]])
     ; [sidebar-item-ui
     ;  (rfe/href :admin.comments/list) [:i.material-icons "comment"] "Comments"]]]])


;;; ---------------------------------------------------------------------------
;;; Navbar

(defn navbar-brand-ui [title]
  [:div.navbar-wrapper
   [:a.navbar-brand {:href "#fix-me"}
     (:title title)]])

(defn navbar-search-ui []
  [:form.navbar-form
   [:div.input-group.no-border
    ; TODO: fix input.
    [:input.form-control
     {:placeholder "Search...", :value "", :type "text" :read-only true}]
    ; TODO: fix input submission.
    [:button.btn.btn-white.btn-round.btn-just-icon
     {:type "submit"}
     [:i.material-icons "search"]
     [:div.ripple-container]]]])

(defn navbar-stats-ui []
  [:li.nav-item
   [:a.nav-link
    {:href "#fix-me"}
    [:i.material-icons "dashboard"]]
   [:p.d-lg-none.d-md-block
    "Stats"]])

(defn navbar-account-ui [current-user]
  [:li.nav-item
   [:a.nav-link
    {:href (rfe/href :profile/view (select-keys @current-user [:users/id]))}
    [:i.material-icons "person"]]
   [:p.d-lg-none.d-md-block
    "Account"]])

(defn navbar-notifications-ui []
  [:li.nav-item.dropdown
   [:a#navbarDropdownMenuLink.nav-link
    {:aria-expanded "false",
     :aria-haspopup "true",
     :data-toggle "dropdown",
     :href "http://fix-me.com"}
    [:i.material-icons "notifications"]
    [:span.notification "5"]]
   [:p.d-lg-none.d-md-block
    "Some Actions"]
   [:div.dropdown-menu.dropdown-menu-right
    {:aria-labelledby "navbarDropdownMenuLink"}
    [:a.dropdown-item
     {:href "#fix-me"}
     "Mike John responded to your email"]
    [:a.dropdown-item {:href "#fix-me"} "You have 5 new tasks"]
    [:a.dropdown-item
     {:href "#fix-me"}
     "You're now friend with Andrew"]
    [:a.dropdown-item {:href "#fix-me"} "Another Notification"]
    [:a.dropdown-item {:href "#fix-me"} "Another One"]]])

(defn navbar-ui [title]
  (r/with-let [current-user (rf/subscribe [:identity])]
    [:nav.navbar.navbar-expand-lg.navbar-transparent.navbar-absolute.fixed-top
     [:div.container-fluid
      [navbar-brand-ui title]
      [:button.navbar-toggler
       {:aria-label "Toggle navigation",
        :aria-expanded "false",
        :aria-controls "navigation-index",
        :data-toggle "collapse",
        :type "button"}
       [:span.sr-only "Toggle navigation"]
       [:span.navbar-toggler-icon.icon-bar]
       [:span.navbar-toggler-icon.icon-bar]
       [:span.navbar-toggler-icon.icon-bar]]]]))
      ; [:div.collapse.navbar-collapse.justify-content-end
      ;  [navbar-search-ui]
      ;  [:ul.navbar-nav
      ;   [navbar-stats-ui]
      ;   [navbar-notifications-ui]
      ;   [navbar-account-ui current-user]]]]]))


;;; ---------------------------------------------------------------------------
;;; Footer

(defn footer-ui []
  [:footer.footer
    [:div.container-fluid
     [:nav.float-left
      [:ul
       [:li>a
         {:href "#/"}
         "Elogger"]]]
       ; [:li>a
       ;   {:href "/about"}
       ;   "About Us"]
       ; [:li>a
       ;   {:href "http://blog.creative-tim.com"}
       ;   "Blog"]
       ; [:li>a
       ;   {:href "https://www.creative-tim.com/license"}
       ;   "Licenses"]]]
     [:div.copyright.float-right
      "© " (.getFullYear (js/Date.))
      ", made with "
      [:i.material-icons "favorite"]
      " by "
      [:a
       {:target "_blank", :href "https://github.com/efraimmgon/elogger"}
       "Elogger"]
      " for a better web."]]])

;;; ---------------------------------------------------------------------------
;;; Base comps

(defn admin-base-ui [& forms]
  [with-deps
   {:deps (<sub [:admin/deps])
    :loading [:div.loading "loading ..."]
    :loaded (into
             [:div
              [modal-ui]
              [error-modal-ui]]
             forms)}])

(defn admin-page-ui [title panel-ui]
  [admin-base-ui
   [:div.wrapper
    [sidebar-ui]
    [:div.main-panel
     [navbar-ui title]
     [:div.content
      [:div.container-fluid
       panel-ui]]
     [footer-ui]]]])
