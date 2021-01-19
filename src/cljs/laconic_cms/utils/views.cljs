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
  [:li
   [:a attrs
    title]])

(defn dropdown-nav-item [title & navs]
  [:li.dropdown
   [:a.dropdown-toggle
    {:data-toggle "dropdown"}
    title]
   (into
     [:ul.dropdown-menu.dropdown-with-icons]
     navs)])

(defn nav-menu [current-user]
  [:div.collapse.navbar-collapse
   [:ul.nav.navbar-nav.navbar-right
    [nav-item {:href (rfe/href :home)} "Home"]
    [nav-item {:href (rfe/href :blog/list)} "Blog"]
    (when @current-user
      [dropdown-nav-item
       (:users/username @current-user)
       [nav-item {:href (rfe/href :profile/view (select-keys @current-user [:users/id]))}
        "Profile"]
       (when (:users/admin @current-user)
         [nav-item {:href (rfe/href :admin)}
          "Admin"])
       [nav-item {:on-click #(dispatch-n [:auth/logout]
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
    [:nav.navbar.navbar-fixed-top.navbar-default
     {:role "navigation"}
     [:div.container
      (comment "<!-- Brand and toggle get grouped for better mobile display -->")
      [:div.navbar-header
       [:button#menu-toggle.navbar-toggle
        {:type "button"}
        [:span.sr-only "Toggle navigation"]
        [:span.icon-bar.bar1]
        [:span.icon-bar.bar2]
        [:span.icon-bar.bar3]]
       [:a.navbar-brand
        {:href "/"}
        "Laconic CMS"]]
      (comment "<!-- Collect the nav links, forms, and other content for toggling -->")
      [nav-menu current-user]]]))

(defn footer []
 [:footer.footer.footer-black
  [:div.container
   [:nav.pull-left
    [:ul
     [:li
      [:a
       {:href "#"}
       "Home"]]
     [:li
      [:a
       {:href "#"}
       "Company"]]
     [:li
      [:a
       {:href "#"}
       "Portfolio"]]
     [:li
      [:a
       {:href "#"}
       "Blog"]]]]
   [:div.social-area.pull-right
    [:a.btn.btn-social.btn-facebook.btn-simple
     [:i.fa.fa-facebook-square]]
    [:a.btn.btn-social.btn-twitter.btn-simple [:i.fa.fa-twitter]]
    [:a.btn.btn-social.btn-pinterest.btn-simple [:i.fa.fa-pinterest]]]
   [:div.copyright
    (str "© " (.getFullYear (js/Date.)) " Laconic CMS")]]])

(defn footer-2 []
 [:footer.footer.footer-big.footer-black
  [:div.container
   [:div.row
    [:div.col-md-2
     [:div.form-group
      [:select.selectpicker
       {:data-menu-style "dropdown-blue",
        :data-style "btn-default btn-block",
        :name "huge"
        :default-value "en"}
       [:option {:value "id"} "Bahasa Indonesia"]
       [:option {:value "ms"} "Bahasa Melayu"]
       [:option {:value "ca"} "Català"]
       [:option {:value "da"} "Dansk"]
       [:option {:value "de"} "Deutsch"]
       [:option {:value "en"} "English"]
       [:option {:value "es"} "Español"]
       [:option {:value "el"} "Eλληνικά"]
       [:option {:value "fr"} "Français"]
       [:option {:value "it"} "Italiano"]
       [:option {:value "hu"} "Magyar"]
       [:option {:value "nl"} "Nederlands"]
       [:option {:value "no"} "Norsk"]
       [:option {:value "pl"} "Polski"]
       [:option {:value "pt"} "Português"]
       [:option {:value "fi"} "Suomi"]
       [:option {:value "sv"} "Svenska"]
       [:option {:value "tr"} "Türkçe"]
       [:option {:value "is"} "Íslenska"]
       [:option {:value "cs"} "Čeština"]
       [:option {:value "ru"} "Русский"]
       [:option {:value "th"} "ภาษาไทย"]
       [:option {:value "zh"} "中文 (简体)"]
       [:option {:value "zh-TW"} "中文 (繁體)"]
       [:option {:value "ja"} "日本語"]
       [:option {:value "ko"} "한국어"]]]
     [:div.form-group
      [:select.selectpicker
       {:data-menu-style "dropdown-blue",
        :data-style "btn-default btn-block",
        :name "huge"
        :default-value "USD"}
       [:option {:value "ARS"} "ARS"]
       [:option {:value "AUD"} "AUD"]
       [:option {:value "BRL"} "BRL"]
       [:option {:value "CAD"} "CAD"]
       [:option {:value "CHF"} "CHF"]
       [:option {:value "CNY"} "CNY"]
       [:option {:value "CZK"} "CZK"]
       [:option {:value "DKK"} "DKK"]
       [:option {:value "EUR"} "EUR"]
       [:option {:value "GBP"} "GBP"]
       [:option {:value "HKD"} "HKD"]
       [:option {:value "HUF"} "HUF"]
       [:option {:value "IDR"} "IDR"]
       [:option {:value "ILS"} "ILS"]
       [:option {:value "INR"} "INR"]
       [:option {:value "JPY"} "JPY"]
       [:option {:value "KRW"} "KRW"]
       [:option {:value "MYR"} "MYR"]
       [:option {:value "MXN"} "MXN"]
       [:option {:value "NOK"} "NOK"]
       [:option {:value "NZD"} "NZD"]
       [:option {:value "PHP"} "PHP"]
       [:option {:value "PLN"} "PLN"]
       [:option {:value "RUB"} "RUB"]
       [:option {:value "SEK"} "SEK"]
       [:option {:value "SGD"} "SGD"]
       [:option {:value "TWD"} "TWD"]
       [:option {:value "USD"} "USD"]
       [:option {:value "VND"} "VND"]
       [:option {:value "ZAR"} "ZAR"]]]]
    [:div.col-md-2.col-md-offset-1
     [:h5.title "Company"]
     [:nav
      [:ul
       [:li
        [:a
         {:href "#"}
         "\n                                    Home\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                   Find offers\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                    Discover Projects\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                    Our Portfolio\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                    About Us\n                                "]]]]]
    [:div.col-md-3.col-md-offset-1
     [:h5.title " Help and Support"]
     [:nav
      [:ul
       [:li
        [:a
         {:href "#"}
         "\n                                   Contact Us\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                   How it works\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                    Terms & Conditions\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                    Company Policy\n                                "]]
       [:li
        [:a
         {:href "#"}
         "\n                                   Money Back\n                                "]]]]]
    [:div.col-md-3
     [:h5.title "Latest News"]
     [:nav
      [:ul
       [:li
        [:a
         {:href "#"}
         [:i.fa.fa-twitter]
         " "
         [:b "Get Shit Done"]
         "\n                                   The best kit in the market is here, just give it a try and let us...\n                                   "]
        [:hr.hr-small]]
       [:li
        [:a
         {:href "#"}
         [:i.fa.fa-twitter]
         "\n                                   We've just been featured on "
         [:b " Awwwards Website"]
         "! Thank you everybody for...\n                                "]]]]]]
   [:hr]
   [:div.social-area.text-center
    [:h5 "Join us on"]
    [:a.btn.btn-social.btn-round {:href "#"} [:i.fa.fa-facebook]]
    [:a.btn.btn-social.btn-round {:href "#"} [:i.fa.fa-twitter]]
    [:a.btn.btn-social.btn-round {:href "#"} [:i.fa.fa-google-plus]]
    [:a.btn.btn-social.btn-round {:href "#"} [:i.fa.fa-pinterest]]
    [:a.btn.btn-social.btn-round {:href "#"} [:i.fa.fa-linkedin]]]
   [:div.copyright
    "\n                © 2016 Creative Tim, made with love\n            "]]])



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
   [footer-2]])
