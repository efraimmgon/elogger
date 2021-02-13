(ns elogger.apps.dashboard
  (:require
    [re-frame.core :as rf]
    [reagent.core :as r]
    [reitit.frontend.easy :as rfe]))

(comment
  "deps and assets"
  "<!--     Fonts and icons     -->"
   [:link
    {:href
     "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons",
     :type "text/css",
     :rel "stylesheet"}]
   [:link
    {:href
     "https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css",
     :rel "stylesheet"}]
   "<!-- CSS Files -->"
   [:link
    {:rel "stylesheet",
     :href "../assets/css/material-dashboard.min.css?v=2.2.2"}])

(defn nav-item [uri title page]
  [:li.nav-item
   [:a.nav-link
    {:href uri
     :class (when (= page @(rf/subscribe [:common/page])) :active)}
    title]])
    

(defn sidebar []
  [:div.sidebar
   {:data-image "/dashboard/img/sidebar-1.jpg",
    :data-background-color "black",
    :data-color "rose"}
   ; Tip 1: You can change the color of the sidebar using: data-color=\"purple | azure | green | orange | danger
   ;  Tip 2: you can also add an image using data-image tag
   [:div.logo
    [:a.simple-text.logo-mini
     {:href "/"}
     "\n          CT\n        "]
    [:a.simple-text.logo-normal
     {:href "/"}
     "eLogger"]]
   [:div.sidebar-wrapper
    [:div.user
     ;; TODO: user photo
     [:div.photo [:img {:src "/dashboard/img/faces/avatar.jpg"}]]
     [:div.user-info
      [:a.username
       {:href "#collapseExample", :data-toggle "collapse"}
       ;; TODO: username
       [:span
        "Tania Andrew"]]]]
    ;; TABS
    [:ul.nav
     ; Dashboard shall show:
     ; latest logins
     [nav-link (rfe/href :admin/dashboard) "Painel"]
     [nav-link (rfe/href :admin/users) "Usuários"]]]])

(defn navbar []
   [:nav.navbar.navbar-expand-lg.navbar-transparent.navbar-absolute.fixed-top
    [:div.container-fluid
     [:div.navbar-wrapper
      [:div.navbar-minimize
       [:button#minimizeSidebar.btn.btn-just-icon.btn-white.btn-fab.btn-round
        [:i.material-icons.text_align-center.visible-on-sidebar-regular
         "more_vert"]
        [:i.material-icons.design_bullet-list-67.visible-on-sidebar-mini
         "view_list"]]]
      [:a.navbar-brand {:href "javascript:;"} "Dashboard"]]
     [:button.navbar-toggler
      {:aria-label "Toggle navigation",
       :aria-expanded "false",
       :aria-controls "navigation-index",
       :data-toggle "collapse",
       :type "button"}
      [:span.sr-only "Toggle navigation"]
      [:span.navbar-toggler-icon.icon-bar]
      [:span.navbar-toggler-icon.icon-bar]
      [:span.navbar-toggler-icon.icon-bar]]
     [:div.collapse.navbar-collapse.justify-content-end
      [:form.navbar-form
       [:div.input-group.no-border
        [:input.form-control
         {:placeholder "Search...", :value "", :type "text"}]
        [:button.btn.btn-white.btn-round.btn-just-icon
         {:type "submit"}
         [:i.material-icons "search"]
         [:div.ripple-container]]]]
      [:ul.navbar-nav
       [:li.nav-item
        [:a.nav-link
         {:href "javascript:;"}
         [:i.material-icons "dashboard"]]
        [:p.d-lg-none.d-md-block
         "\n                    Stats\n                  "]]
       [:li.nav-item.dropdown
        [:a#navbarDropdownMenuLink.nav-link
         {:aria-expanded "false",
          :aria-haspopup "true",
          :data-toggle "dropdown",
          :href "http://example.com"}
         [:i.material-icons "notifications"]
         [:span.notification "5"]]
        [:p.d-lg-none.d-md-block
         "\n                    Some Actions\n                  "]
        [:div.dropdown-menu.dropdown-menu-right
         {:aria-labelledby "navbarDropdownMenuLink"}
         [:a.dropdown-item
          {:href "#"}
          "Mike John responded to your email"]
         [:a.dropdown-item {:href "#"} "You have 5 new tasks"]
         [:a.dropdown-item
          {:href "#"}
          "You're now friend with Andrew"]
         [:a.dropdown-item {:href "#"} "Another Notification"]
         [:a.dropdown-item {:href "#"} "Another One"]]]
       [:li.nav-item.dropdown
        [:a#navbarDropdownProfile.nav-link
         {:aria-expanded "false",
          :aria-haspopup "true",
          :data-toggle "dropdown",
          :href "javascript:;"}
         [:i.material-icons "person"]]
        [:p.d-lg-none.d-md-block
         "\n                    Account\n                  "]
        [:div.dropdown-menu.dropdown-menu-right
         {:aria-labelledby "navbarDropdownProfile"}
         [:a.dropdown-item {:href "#"} "Profile"]
         [:a.dropdown-item {:href "#"} "Settings"]
         [:div.dropdown-divider]
         [:a.dropdown-item {:href "#"} "Log out"]]]]]]])


(defn dashboard []
  (r/with-let [current-user (rf/subscribe [:identity])])
  [:body
   [:div.wrapper
    [sidebar]
    [:div.main-panel
     [navbar]
     [:div.content
      [:div.content
       [:div.container-fluid
        [:div.row
         [:div.col-md-12
          [:div.card
           [:div.card-header.card-header-success.card-header-icon
            [:div.card-icon [:i.material-icons ""]]
            [:h4.card-title "Global Sales by Top Locations"]]
           [:div.card-body
            [:div.row
             [:div.col-md-6
              [:div.table-responsive.table-sales
               [:table.table
                [:tbody
                 [:tr
                  [:td
                   [:div.flag
                    [:img
                     {:div "div",
                      :_ "_",
                      :src "../assets/img/flags/US.png"}]]]
                  [:td "USA"]
                  [:td.text-right
                   "\n                                  2.920\n                                "]
                  [:td.text-right
                   "\n                                  53.23%\n                                "]]
                 [:tr
                  [:td
                   [:div.flag
                    [:img
                     {:div "div",
                      :_ "_",
                      :src "../assets/img/flags/DE.png"}]]]
                  [:td "Germany"]
                  [:td.text-right
                   "\n                                  1.300\n                                "]
                  [:td.text-right
                   "\n                                  20.43%\n                                "]]
                 [:tr
                  [:td
                   [:div.flag
                    [:img
                     {:div "div",
                      :_ "_",
                      :src "../assets/img/flags/AU.png"}]]]
                  [:td "Australia"]
                  [:td.text-right
                   "\n                                  760\n                                "]
                  [:td.text-right
                   "\n                                  10.35%\n                                "]]
                 [:tr
                  [:td
                   [:div.flag
                    [:img
                     {:div "div",
                      :_ "_",
                      :src "../assets/img/flags/GB.png"}]]]
                  [:td "United Kingdom"]
                  [:td.text-right
                   "\n                                  690\n                                "]
                  [:td.text-right
                   "\n                                  7.87%\n                                "]]
                 [:tr
                  [:td
                   [:div.flag
                    [:img
                     {:div "div",
                      :_ "_",
                      :src "../assets/img/flags/RO.png"}]]]
                  [:td "Romania"]
                  [:td.text-right
                   "\n                                  600\n                                "]
                  [:td.text-right
                   "\n                                  5.94%\n                                "]]
                 [:tr
                  [:td
                   [:div.flag
                    [:img
                     {:div "div",
                      :_ "_",
                      :src "../assets/img/flags/BR.png"}]]]
                  [:td "Brasil"]
                  [:td.text-right
                   "\n                                  550\n                                "]
                  [:td.text-right
                   "\n                                  4.34%\n                                "]]]]]]
             [:div.col-md-6.ml-auto.mr-auto
              [:div#worldMap {:style "height: 300px;"}]]]]]]]
        "<!-- <button type=\"button\" class=\"btn btn-round btn-default dropdown-toggle btn-link\" data-toggle=\"dropdown\">\n7 days\n</button> -->"
        [:div.row
         [:div.col-md-4
          [:div.card.card-chart
           [:div.card-header.card-header-rose
            {:data-header-animation "true"}
            [:div#websiteViewsChart.ct-chart]]
           [:div.card-body
            [:div.card-actions
             [:button.btn.btn-danger.btn-link.fix-broken-card
              {:type "button"}
              [:i.material-icons "build"]
              " Fix Header!\n                      "]
             [:button.btn.btn-info.btn-link
              {:title "Refresh",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "refresh"]]
             [:button.btn.btn-default.btn-link
              {:title "Change Date",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "edit"]]]
            [:h4.card-title "Website Views"]
            [:p.card-category "Last Campaign Performance"]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons "access_time"]
             " campaign sent 2 days ago\n                    "]]]]
         [:div.col-md-4
          [:div.card.card-chart
           [:div.card-header.card-header-success
            {:data-header-animation "true"}
            [:div#dailySalesChart.ct-chart]]
           [:div.card-body
            [:div.card-actions
             [:button.btn.btn-danger.btn-link.fix-broken-card
              {:type "button"}
              [:i.material-icons "build"]
              " Fix Header!\n                      "]
             [:button.btn.btn-info.btn-link
              {:title "Refresh",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "refresh"]]
             [:button.btn.btn-default.btn-link
              {:title "Change Date",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "edit"]]]
            [:h4.card-title "Daily Sales"]
            [:p.card-category
             [:span.text-success [:i.fa.fa-long-arrow-up] " 55% "]
             " increase in today sales."]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons "access_time"]
             " updated 4 minutes ago\n                    "]]]]
         [:div.col-md-4
          [:div.card.card-chart
           [:div.card-header.card-header-info
            {:data-header-animation "true"}
            [:div#completedTasksChart.ct-chart]]
           [:div.card-body
            [:div.card-actions
             [:button.btn.btn-danger.btn-link.fix-broken-card
              {:type "button"}
              [:i.material-icons "build"]
              " Fix Header!\n                      "]
             [:button.btn.btn-info.btn-link
              {:title "Refresh",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "refresh"]]
             [:button.btn.btn-default.btn-link
              {:title "Change Date",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "edit"]]]
            [:h4.card-title "Completed Tasks"]
            [:p.card-category "Last Campaign Performance"]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons "access_time"]
             " campaign sent 2 days ago\n                    "]]]]]
        [:div.row
         [:div.col-lg-3.col-md-6.col-sm-6
          [:div.card.card-stats
           [:div.card-header.card-header-warning.card-header-icon
            [:div.card-icon [:i.material-icons "weekend"]]
            [:p.card-category "Bookings"]
            [:h3.card-title "184"]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons.text-danger "warning"]
             [:a {:href "#pablo"} "Get More Space..."]]]]]
         [:div.col-lg-3.col-md-6.col-sm-6
          [:div.card.card-stats
           [:div.card-header.card-header-rose.card-header-icon
            [:div.card-icon [:i.material-icons "equalizer"]]
            [:p.card-category "Website Visits"]
            [:h3.card-title "75.521"]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons "local_offer"]
             " Tracked from Google Analytics\n                    "]]]]
         [:div.col-lg-3.col-md-6.col-sm-6
          [:div.card.card-stats
           [:div.card-header.card-header-success.card-header-icon
            [:div.card-icon [:i.material-icons "store"]]
            [:p.card-category "Revenue"]
            [:h3.card-title "$34,245"]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons "date_range"]
             " Last 24 Hours\n                    "]]]]
         [:div.col-lg-3.col-md-6.col-sm-6
          [:div.card.card-stats
           [:div.card-header.card-header-info.card-header-icon
            [:div.card-icon [:i.fa.fa-twitter]]
            [:p.card-category "Followers"]
            [:h3.card-title "+245"]]
           [:div.card-footer
            [:div.stats
             [:i.material-icons "update"]
             " Just Updated\n                    "]]]]]
        [:h3 "Manage Listings"]
        [:br]
        [:div.row
         [:div.col-md-4
          [:div.card.card-product
           [:div.card-header.card-header-image
            {:data-header-animation "true"}
            [:a
             {:href "#pablo"}
             [:img.img {:src "../assets/img/card-2.jpg"}]]]
           [:div.card-body
            [:div.card-actions.text-center
             [:button.btn.btn-danger.btn-link.fix-broken-card
              {:type "button"}
              [:i.material-icons "build"]
              " Fix Header!\n                      "]
             [:button.btn.btn-default.btn-link
              {:title "View",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "art_track"]]
             [:button.btn.btn-success.btn-link
              {:title "Edit",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "edit"]]
             [:button.btn.btn-danger.btn-link
              {:title "Remove",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "close"]]]
            [:h4.card-title
             [:a {:href "#pablo"} "Cozy 5 Stars Apartment"]]
            [:div.card-description
             "\n                      The place is close to Barceloneta Beach and bus stop just 2 min by walk and near to \"Naviglio\" where you can enjoy the main night life in Barcelona.\n                    "]]
           [:div.card-footer
            [:div.price [:h4 "$899/night"]]
            [:div.stats
             [:p.card-category
              [:i.material-icons "place"]
              " Barcelona, Spain"]]]]]
         [:div.col-md-4
          [:div.card.card-product
           [:div.card-header.card-header-image
            {:data-header-animation "true"}
            [:a
             {:href "#pablo"}
             [:img.img {:src "../assets/img/card-3.jpg"}]]]
           [:div.card-body
            [:div.card-actions.text-center
             [:button.btn.btn-danger.btn-link.fix-broken-card
              {:type "button"}
              [:i.material-icons "build"]
              " Fix Header!\n                      "]
             [:button.btn.btn-default.btn-link
              {:title "View",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "art_track"]]
             [:button.btn.btn-success.btn-link
              {:title "Edit",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "edit"]]
             [:button.btn.btn-danger.btn-link
              {:title "Remove",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "close"]]]
            [:h4.card-title [:a {:href "#pablo"} "Office Studio"]]
            [:div.card-description
             "\n                      The place is close to Metro Station and bus stop just 2 min by walk and near to \"Naviglio\" where you can enjoy the night life in London, UK.\n                    "]]
           [:div.card-footer
            [:div.price [:h4 "$1.119/night"]]
            [:div.stats
             [:p.card-category
              [:i.material-icons "place"]
              " London, UK"]]]]]
         [:div.col-md-4
          [:div.card.card-product
           [:div.card-header.card-header-image
            {:data-header-animation "true"}
            [:a
             {:href "#pablo"}
             [:img.img {:src "../assets/img/card-1.jpg"}]]]
           [:div.card-body
            [:div.card-actions.text-center
             [:button.btn.btn-danger.btn-link.fix-broken-card
              {:type "button"}
              [:i.material-icons "build"]
              " Fix Header!\n                      "]
             [:button.btn.btn-default.btn-link
              {:title "View",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "art_track"]]
             [:button.btn.btn-success.btn-link
              {:title "Edit",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "edit"]]
             [:button.btn.btn-danger.btn-link
              {:title "Remove",
               :data-placement "bottom",
               :rel "tooltip",
               :type "button"}
              [:i.material-icons "close"]]]
            [:h4.card-title [:a {:href "#pablo"} "Beautiful Castle"]]
            [:div.card-description
             "\n                      The place is close to Metro Station and bus stop just 2 min by walk and near to \"Naviglio\" where you can enjoy the main night life in Milan.\n                    "]]
           [:div.card-footer
            [:div.price [:h4 "$459/night"]]
            [:div.stats
             [:p.card-category
              [:i.material-icons "place"]
              " Milan, Italy"]]]]]]]]]
     [:footer.footer
      [:div.container-fluid
       [:nav.float-left
        [:ul
         [:li
          [:a
           {:href "https://www.creative-tim.com"}
           "\n                  Creative Tim\n                "]]
         [:li
          [:a
           {:href "https://creative-tim.com/presentation"}
           "\n                  About Us\n                "]]
         [:li
          [:a
           {:href "http://blog.creative-tim.com"}
           "\n                  Blog\n                "]]
         [:li
          [:a
           {:href "https://www.creative-tim.com/license"}
           "\n                  Licenses\n                "]]]]
       [:div.copyright.float-right
        "\n            ©\n            "
        [:script
         "\n              document.write(new Date().getFullYear())\n            "]
        ", made with "
        [:i.material-icons "favorite"]
        " by\n            "
        [:a
         {:target "_blank", :href "https://www.creative-tim.com"}
         "Creative Tim"]
        " for a better web.\n          "]]]]]
   [:div.fixed-plugin
    [:div.dropdown.show-dropdown
     [:a {:data-toggle "dropdown", :href "#"} [:i.fa.fa-cog.fa-2x " "]]
     [:ul.dropdown-menu
      [:li.header-title " Sidebar Filters"]
      [:li.adjustments-line
       [:a.switch-trigger.active-color {:href "javascript:void(0)"}]
       [:div.badge-colors.ml-auto.mr-auto
        [:span.badge.filter.badge-purple {:data-color "purple"}]
        [:span.badge.filter.badge-azure {:data-color "azure"}]
        [:span.badge.filter.badge-green {:data-color "green"}]
        [:span.badge.filter.badge-warning {:data-color "orange"}]
        [:span.badge.filter.badge-danger {:data-color "danger"}]
        [:span.badge.filter.badge-rose.active {:data-color "rose"}]]
       [:div.clearfix]]
      [:li.header-title "Sidebar Background"]
      [:li.adjustments-line
       [:a.switch-trigger.background-color
        {:href "javascript:void(0)"}]
       [:div.ml-auto.mr-auto
        [:span.badge.filter.badge-black.active
         {:data-background-color "black"}]
        [:span.badge.filter.badge-white
         {:data-background-color "white"}]
        [:span.badge.filter.badge-red {:data-background-color "red"}]]
       [:div.clearfix]]
      [:li.adjustments-line
       [:a.switch-trigger {:href "javascript:void(0)"}]
       [:p "Sidebar Mini"]
       [:label.ml-auto]
       [:div.togglebutton.switch-sidebar-mini
        [:label [:input {:type "checkbox"}] [:span.toggle]]]
       [:div.clearfix]]
      [:li.adjustments-line
       [:a.switch-trigger {:href "javascript:void(0)"}]
       [:p "Sidebar Images"]
       [:label.switch-mini.ml-auto]
       [:div.togglebutton.switch-sidebar-image
        [:label
         [:input {:checked "", :type "checkbox"}]
         [:span.toggle]]]
       [:div.clearfix]]
      [:li.header-title "Images"]
      [:li.active
       [:a.img-holder.switch-trigger
        {:href "javascript:void(0)"}
        [:img {:alt "", :src "../assets/img/sidebar-1.jpg"}]]]
      [:li
       [:a.img-holder.switch-trigger
        {:href "javascript:void(0)"}
        [:img {:alt "", :src "../assets/img/sidebar-2.jpg"}]]]
      [:li
       [:a.img-holder.switch-trigger
        {:href "javascript:void(0)"}
        [:img {:alt "", :src "../assets/img/sidebar-3.jpg"}]]]
      [:li
       [:a.img-holder.switch-trigger
        {:href "javascript:void(0)"}
        [:img {:alt "", :src "../assets/img/sidebar-4.jpg"}]]]
      [:li.button-container
       [:a.btn.btn-rose.btn-block.btn-fill
        {:target "_blank",
         :href
         "https://www.creative-tim.com/product/material-dashboard-pro"}
        "Buy Now"]
       [:a.btn.btn-default.btn-block
        {:target "_blank",
         :href
         "https://demos.creative-tim.com/material-dashboard-pro/docs/2.1/getting-started/introduction.html"}
        "\n            Documentation\n          "]
       [:a.btn.btn-info.btn-block
        {:target "_blank",
         :href
         "https://www.creative-tim.com/product/material-dashboard"}
        "\n            Get Free Demo!\n          "]]
      [:li.button-container.github-star
       [:a.github-button
        {:aria-label "Star ntkme/github-buttons on GitHub",
         :data-show-count "true",
         :data-size "large",
         :data-icon "octicon-star",
         :href
         "https://github.com/creativetimofficial/ct-material-dashboard-pro"}
        "Star"]]
      [:li.header-title "Thank you for 95 shares!"]
      [:li.button-container.text-center
       [:button#twitter.btn.btn-round.btn-twitter
        [:i.fa.fa-twitter]
        " · 45"]
       [:button#facebook.btn.btn-round.btn-facebook
        [:i.fa.fa-facebook-f]
        " · 50"]
       [:br]
       [:br]]]]]
   "<!--   Core JS Files   -->"
   [:script {:src "../assets/js/core/jquery.min.js"}]
   [:script {:src "../assets/js/core/popper.min.js"}]
   [:script
    {:src "../assets/js/core/bootstrap-material-design.min.js"}]
   [:script {:src "../assets/js/plugins/perfect-scrollbar.min.js"}]
   "<!-- Plugin for the momentJs  -->"
   [:script {:src "../assets/js/plugins/moment.min.js"}]
   "<!--  Plugin for Sweet Alert -->"
   [:script {:src "../assets/js/plugins/sweetalert2.js"}]
   "<!-- Forms Validations Plugin -->"
   [:script {:src "../assets/js/plugins/jquery.validate.min.js"}]
   "<!-- Plugin for the Wizard, full documentation here: https://github.com/VinceG/twitter-bootstrap-wizard -->"
   [:script {:src "../assets/js/plugins/jquery.bootstrap-wizard.js"}]
   "<!--\tPlugin for Select, full documentation here: http://silviomoreto.github.io/bootstrap-select -->"
   [:script {:src "../assets/js/plugins/bootstrap-selectpicker.js"}]
   "<!--  Plugin for the DateTimePicker, full documentation here: https://eonasdan.github.io/bootstrap-datetimepicker/ -->"
   [:script
    {:src "../assets/js/plugins/bootstrap-datetimepicker.min.js"}]
   "<!--  DataTables.net Plugin, full documentation here: https://datatables.net/  -->"
   [:script {:src "../assets/js/plugins/jquery.dataTables.min.js"}]
   "<!--\tPlugin for Tags, full documentation here: https://github.com/bootstrap-tagsinput/bootstrap-tagsinputs  -->"
   [:script {:src "../assets/js/plugins/bootstrap-tagsinput.js"}]
   "<!-- Plugin for Fileupload, full documentation here: http://www.jasny.net/bootstrap/javascript/#fileinput -->"
   [:script {:src "../assets/js/plugins/jasny-bootstrap.min.js"}]
   "<!--  Full Calendar Plugin, full documentation here: https://github.com/fullcalendar/fullcalendar    -->"
   [:script {:src "../assets/js/plugins/fullcalendar.min.js"}]
   "<!-- Vector Map plugin, full documentation here: http://jvectormap.com/documentation/ -->"
   [:script {:src "../assets/js/plugins/jquery-jvectormap.js"}]
   "<!--  Plugin for the Sliders, full documentation here: http://refreshless.com/nouislider/ -->"
   [:script {:src "../assets/js/plugins/nouislider.min.js"}]
   "<!-- Include a polyfill for ES6 Promises (optional) for IE11, UC Browser and Android browser support SweetAlert -->"
   [:script
    {:src
     "https://cdnjs.cloudflare.com/ajax/libs/core-js/2.4.1/core.js"}]
   "<!-- Library for adding dinamically elements -->"
   [:script {:src "../assets/js/plugins/arrive.min.js"}]
   "<!--  Google Maps Plugin    -->"
   [:script
    {:src
     "https://maps.googleapis.com/maps/api/js?key=AIzaSyB2Yno10-YTnLjjn_Vtk0V8cdcY5lC4plU"}]
   "<!-- Place this tag in your head or just before your close body tag. -->"
   [:script
    {:src "https://buttons.github.io/buttons.js",
     :defer "defer",
     :async "async"}]
   "<!-- Chartist JS -->"
   [:script {:src "../assets/js/plugins/chartist.min.js"}]
   "<!--  Notifications Plugin    -->"
   [:script {:src "../assets/js/plugins/bootstrap-notify.js"}]
   "<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->"
   [:script
    {:type "text/javascript",
     :src "../assets/js/material-dashboard.min.js?v=2.2.2"}]
   "<!-- Material Dashboard DEMO methods, don't include it in your project! -->"
   [:script
    "\n    $(document).ready(function() {\n      $().ready(function() {\n        $sidebar = $('.sidebar');\n\n        $sidebar_img_container = $sidebar.find('.sidebar-background');\n\n        $full_page = $('.full-page');\n\n        $sidebar_responsive = $('body > .navbar-collapse');\n\n        window_width = $(window).width();\n\n        fixed_plugin_open = $('.sidebar .sidebar-wrapper .nav li.active a p').html();\n\n        if (window_width > 767 && fixed_plugin_open == 'Dashboard') {\n          if ($('.fixed-plugin .dropdown').hasClass('show-dropdown')) {\n            $('.fixed-plugin .dropdown').addClass('open');\n          }\n\n        }\n\n        $('.fixed-plugin a').click(function(event) {\n          // Alex if we click on switch, stop propagation of the event, so the dropdown will not be hide, otherwise we set the  section active\n          if ($(this).hasClass('switch-trigger')) {\n            if (event.stopPropagation) {\n              event.stopPropagation();\n            } else if (window.event) {\n              window.event.cancelBubble = true;\n            }\n          }\n        });\n\n        $('.fixed-plugin .active-color span').click(function() {\n          $full_page_background = $('.full-page-background');\n\n          $(this).siblings().removeClass('active');\n          $(this).addClass('active');\n\n          var new_color = $(this).data('color');\n\n          if ($sidebar.length != 0) {\n            $sidebar.attr('data-color', new_color);\n          }\n\n          if ($full_page.length != 0) {\n            $full_page.attr('filter-color', new_color);\n          }\n\n          if ($sidebar_responsive.length != 0) {\n            $sidebar_responsive.attr('data-color', new_color);\n          }\n        });\n\n        $('.fixed-plugin .background-color .badge').click(function() {\n          $(this).siblings().removeClass('active');\n          $(this).addClass('active');\n\n          var new_color = $(this).data('background-color');\n\n          if ($sidebar.length != 0) {\n            $sidebar.attr('data-background-color', new_color);\n          }\n        });\n\n        $('.fixed-plugin .img-holder').click(function() {\n          $full_page_background = $('.full-page-background');\n\n          $(this).parent('li').siblings().removeClass('active');\n          $(this).parent('li').addClass('active');\n\n\n          var new_image = $(this).find(\"img\").attr('src');\n\n          if ($sidebar_img_container.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {\n            $sidebar_img_container.fadeOut('fast', function() {\n              $sidebar_img_container.css('background-image', 'url(\"' + new_image + '\")');\n              $sidebar_img_container.fadeIn('fast');\n            });\n          }\n\n          if ($full_page_background.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {\n            var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');\n\n            $full_page_background.fadeOut('fast', function() {\n              $full_page_background.css('background-image', 'url(\"' + new_image_full_page + '\")');\n              $full_page_background.fadeIn('fast');\n            });\n          }\n\n          if ($('.switch-sidebar-image input:checked').length == 0) {\n            var new_image = $('.fixed-plugin li.active .img-holder').find(\"img\").attr('src');\n            var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');\n\n            $sidebar_img_container.css('background-image', 'url(\"' + new_image + '\")');\n            $full_page_background.css('background-image', 'url(\"' + new_image_full_page + '\")');\n          }\n\n          if ($sidebar_responsive.length != 0) {\n            $sidebar_responsive.css('background-image', 'url(\"' + new_image + '\")');\n          }\n        });\n\n        $('.switch-sidebar-image input').change(function() {\n          $full_page_background = $('.full-page-background');\n\n          $input = $(this);\n\n          if ($input.is(':checked')) {\n            if ($sidebar_img_container.length != 0) {\n              $sidebar_img_container.fadeIn('fast');\n              $sidebar.attr('data-image', '#');\n            }\n\n            if ($full_page_background.length != 0) {\n              $full_page_background.fadeIn('fast');\n              $full_page.attr('data-image', '#');\n            }\n\n            background_image = true;\n          } else {\n            if ($sidebar_img_container.length != 0) {\n              $sidebar.removeAttr('data-image');\n              $sidebar_img_container.fadeOut('fast');\n            }\n\n            if ($full_page_background.length != 0) {\n              $full_page.removeAttr('data-image', '#');\n              $full_page_background.fadeOut('fast');\n            }\n\n            background_image = false;\n          }\n        });\n\n        $('.switch-sidebar-mini input').change(function() {\n          $body = $('body');\n\n          $input = $(this);\n\n          if (md.misc.sidebar_mini_active == true) {\n            $('body').removeClass('sidebar-mini');\n            md.misc.sidebar_mini_active = false;\n\n            if ($(\".sidebar\").length != 0) {\n              var ps = new PerfectScrollbar('.sidebar');\n            }\n            if ($(\".sidebar-wrapper\").length != 0) {\n              var ps1 = new PerfectScrollbar('.sidebar-wrapper');\n            }\n            if ($(\".main-panel\").length != 0) {\n              var ps2 = new PerfectScrollbar('.main-panel');\n            }\n            if ($(\".main\").length != 0) {\n              var ps3 = new PerfectScrollbar('main');\n            }\n\n          } else {\n\n            if ($(\".sidebar\").length != 0) {\n              var ps = new PerfectScrollbar('.sidebar');\n              ps.destroy();\n            }\n            if ($(\".sidebar-wrapper\").length != 0) {\n              var ps1 = new PerfectScrollbar('.sidebar-wrapper');\n              ps1.destroy();\n            }\n            if ($(\".main-panel\").length != 0) {\n              var ps2 = new PerfectScrollbar('.main-panel');\n              ps2.destroy();\n            }\n            if ($(\".main\").length != 0) {\n              var ps3 = new PerfectScrollbar('main');\n              ps3.destroy();\n            }\n\n\n            setTimeout(function() {\n              $('body').addClass('sidebar-mini');\n\n              md.misc.sidebar_mini_active = true;\n            }, 300);\n          }\n\n          // we simulate the window Resize so the charts will get updated in realtime.\n          var simulateWindowResize = setInterval(function() {\n            window.dispatchEvent(new Event('resize'));\n          }, 180);\n\n          // we stop the simulation of Window Resize after the animations are completed\n          setTimeout(function() {\n            clearInterval(simulateWindowResize);\n          }, 1000);\n\n        });\n      });\n    });\n  "]
   "<!-- Sharrre libray -->"
   [:script {:src "../assets/demo/jquery.sharrre.js"}]
   [:script
    "\n    $(document).ready(function() {\n\n\n      $('#facebook').sharrre({\n        share: {\n          facebook: true\n        },\n        enableHover: false,\n        enableTracking: false,\n        enableCounter: false,\n        click: function(api, options) {\n          api.simulateClick();\n          api.openPopup('facebook');\n        },\n        template: '<i class=\"fab fa-facebook-f\"></i> Facebook',\n        url: 'https://demos.creative-tim.com/material-dashboard-pro/examples/dashboard.html'\n      });\n\n      $('#google').sharrre({\n        share: {\n          googlePlus: true\n        },\n        enableCounter: false,\n        enableHover: false,\n        enableTracking: true,\n        click: function(api, options) {\n          api.simulateClick();\n          api.openPopup('googlePlus');\n        },\n        template: '<i class=\"fab fa-google-plus\"></i> Google',\n        url: 'https://demos.creative-tim.com/material-dashboard-pro/examples/dashboard.html'\n      });\n\n      $('#twitter').sharrre({\n        share: {\n          twitter: true\n        },\n        enableHover: false,\n        enableTracking: false,\n        enableCounter: false,\n        buttons: {\n          twitter: {\n            via: 'CreativeTim'\n          }\n        },\n        click: function(api, options) {\n          api.simulateClick();\n          api.openPopup('twitter');\n        },\n        template: '<i class=\"fab fa-twitter\"></i> Twitter',\n        url: 'https://demos.creative-tim.com/material-dashboard-pro/examples/dashboard.html'\n      });\n\n\n      // Facebook Pixel Code Don't Delete\n      ! function(f, b, e, v, n, t, s) {\n        if (f.fbq) return;\n        n = f.fbq = function() {\n          n.callMethod ?\n            n.callMethod.apply(n, arguments) : n.queue.push(arguments)\n        };\n        if (!f._fbq) f._fbq = n;\n        n.push = n;\n        n.loaded = !0;\n        n.version = '2.0';\n        n.queue = [];\n        t = b.createElement(e);\n        t.async = !0;\n        t.src = v;\n        s = b.getElementsByTagName(e)[0];\n        s.parentNode.insertBefore(t, s)\n      }(window,\n        document, 'script', '//connect.facebook.net/en_US/fbevents.js');\n\n      try {\n        fbq('init', '111649226022273');\n        fbq('track', \"PageView\");\n\n      } catch (err) {\n        console.log('Facebook Track Error:', err);\n      }\n\n    });\n  "]
   [:script
    "\n    // Facebook Pixel Code Don't Delete\n    ! function(f, b, e, v, n, t, s) {\n      if (f.fbq) return;\n      n = f.fbq = function() {\n        n.callMethod ?\n          n.callMethod.apply(n, arguments) : n.queue.push(arguments)\n      };\n      if (!f._fbq) f._fbq = n;\n      n.push = n;\n      n.loaded = !0;\n      n.version = '2.0';\n      n.queue = [];\n      t = b.createElement(e);\n      t.async = !0;\n      t.src = v;\n      s = b.getElementsByTagName(e)[0];\n      s.parentNode.insertBefore(t, s)\n    }(window,\n      document, 'script', '//connect.facebook.net/en_US/fbevents.js');\n\n    try {\n      fbq('init', '111649226022273');\n      fbq('track', \"PageView\");\n\n    } catch (err) {\n      console.log('Facebook Track Error:', err);\n    }\n  "]
   [:noscript
    [:img
     {:src
      "https://www.facebook.com/tr?id=111649226022273&ev=PageView&noscript=1",
      :style "display:none",
      :width "1",
      :height "1"}]]
   [:script
    "\n    $(document).ready(function() {\n      // Javascript method's body can be found in assets/js/demos.js\n      md.initDashboardPageCharts();\n\n      md.initVectorMap();\n\n    });\n  "]])
