(ns laconic-cms.apps.admin.dashboard)



; -----------------------------------------------------------------------------
; Content

(defn get-more-space-card []
  [:div.col-lg-3.col-md-6.col-sm-6
   [:div.card.card-stats
    [:div.card-header.card-header-warning.card-header-icon
     [:div.card-icon [:i.material-icons "content_copy"]]
     [:p.card-category "Used Space"]
     [:h3.card-title
      "49/50 "
      [:small "GB"]]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons.text-danger "warning"]
      [:a {:href "#fix-me"} "Get More Space..."]]]]])

(defn last-24h-revenue-card []
  [:div.col-lg-3.col-md-6.col-sm-6
   [:div.card.card-stats
    [:div.card-header.card-header-success.card-header-icon
     [:div.card-icon [:i.material-icons "store"]]
     [:p.card-category "Revenue"]
     [:h3.card-title "$34,245"]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons "date_range"]
      "Last 24 Hours"]]]])

(defn fixed-issues-card []
  [:div.col-lg-3.col-md-6.col-sm-6
   [:div.card.card-stats
    [:div.card-header.card-header-danger.card-header-icon
     [:div.card-icon [:i.material-icons "info_outline"]]
     [:p.card-category "Fixed Issues"]
     [:h3.card-title "75"]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons "local_offer"]
      " Tracked from Github"]]]])

(defn followers-card []
  [:div.col-lg-3.col-md-6.col-sm-6
   [:div.card.card-stats
    [:div.card-header.card-header-info.card-header-icon
     [:div.card-icon [:i.fa.fa-twitter]]
     [:p.card-category "Followers"]
     [:h3.card-title "+245"]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons "update"]
      " Just Updated"]]]])

(defn daily-sales []
  [:div.col-md-4
   [:div.card.card-chart
    [:div.card-header.card-header-success
     [:div#dailySalesChart.ct-chart]]
    [:div.card-body
     [:h4.card-title "Daily Sales"]
     [:p.card-category
      [:span.text-success [:i.fa.fa-long-arrow-up] " 55% "]
      " increase in today sales."]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons "access_time"]
      " updated 4 minutes ago"]]]])

(defn email-subscriptions []
  [:div.col-md-4
   [:div.card.card-chart
    [:div.card-header.card-header-warning
     [:div#websiteViewsChart.ct-chart]]
    [:div.card-body
     [:h4.card-title "Email Subscriptions"]
     [:p.card-category "Last Campaign Performance"]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons "access_time"]
      " campaign sent 2 days ago"]]]])

(defn completed-tasks []
  [:div.col-md-4
   [:div.card.card-chart
    [:div.card-header.card-header-danger
     [:div#completedTasksChart.ct-chart]]
    [:div.card-body
     [:h4.card-title "Completed Tasks"]
     [:p.card-category "Last Campaign Performance"]]
    [:div.card-footer
     [:div.stats
      [:i.material-icons "access_time"]
      " campaign sent 2 days ago"]]]])

(defn tasks-header []
  [:div.card-header.card-header-tabs.card-header-primary
   [:div.nav-tabs-navigation
    [:div.nav-tabs-wrapper
     [:span.nav-tabs-title "Tasks:"]
     [:ul.nav.nav-tabs
      {:data-tabs "tabs"}
      [:li.nav-item
       [:a.nav-link.active
        {:data-toggle "tab", :href "#profile"}
        [:i.material-icons "bug_report"]
        " Bugs"]
       [:div.ripple-container]]
      [:li.nav-item
       [:a.nav-link
        {:data-toggle "tab", :href "#messages"}
        [:i.material-icons "code"]
        " Website"]
       [:div.ripple-container]]
      [:li.nav-item
       [:a.nav-link
        {:data-toggle "tab", :href "#settings"}
        [:i.material-icons "cloud"]
        " Server"]
       [:div.ripple-container]]]]]])

(defn tasks-bugs []
  [:div#profile.tab-pane.active
   [:table.table
    [:tbody
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:checked "checked", :value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Sign contract for \"What are conference organizers afraid of?\""]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Lines From Great Russian Literature? Or E-mails From My Boss?"]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Flooded: One year later, assessing what was lost and what was found when a ravaging rain swept through metro Detroit"]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:checked "checked", :value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Create 4 Invisible User Experiences you Never Knew About"]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]]]])

(defn tasks-website []
  [:div#messages.tab-pane
   [:table.table
    [:tbody
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:checked "checked", :value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Flooded: One year later, assessing what was lost and what was found when a ravaging rain swept through metro Detroit"]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Sign contract for \"What are conference organizers afraid of?\""]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]]]])

(defn tasks-server []
  [:div#settings.tab-pane
   [:table.table
    [:tbody
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Lines From Great Russian Literature? Or E-mails From My Boss?"]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:checked "checked", :value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Flooded: One year later, assessing what was lost and what was found when a ravaging rain swept through metro Detroit"]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]
     [:tr
      [:td
       [:div.form-check
        [:label.form-check-label
         [:input.form-check-input
          {:checked "checked", :value "" :read-only true, :type "checkbox"}]
         [:span.form-check-sign [:span.check]]]]]
      [:td
       "Sign contract for \"What are conference organizers afraid of?\""]
      [:td.td-actions.text-right
       [:button.btn.btn-primary.btn-link.btn-sm
        {:title "Edit Task", :rel "tooltip", :type "button"}
        [:i.material-icons "edit"]]
       [:button.btn.btn-danger.btn-link.btn-sm
        {:title "Remove", :rel "tooltip", :type "button"}
        [:i.material-icons "close"]]]]]]])

(defn tasks []
  [:div.col-lg-6.col-md-12
   [:div.card
    [tasks-header]
    [:div.card-body
     [:div.tab-content
      [tasks-bugs]
      [tasks-website]
      [tasks-server]]]]])

(defn employees-stats []
  [:div.col-lg-6.col-md-12
   [:div.card
    [:div.card-header.card-header-warning
     [:h4.card-title "Employees Stats"]
     [:p.card-category "New employees on 15th September, 2016"]]
    [:div.card-body.table-responsive
     [:table.table.table-hover
      [:thead.text-warning
       [:tr
        [:th "ID"]
        [:th "Name"]
        [:th "Salary"]
        [:th "Country"]]]
      [:tbody
       [:tr
        [:td "1"]
        [:td "Dakota Rice"]
        [:td "$36,738"]
        [:td "Niger"]]
       [:tr
        [:td "2"]
        [:td "Minerva Hooper"]
        [:td "$23,789"]
        [:td "Curaçao"]]
       [:tr
        [:td "3"]
        [:td "Sage Rodriguez"]
        [:td "$56,142"]
        [:td "Netherlands"]]
       [:tr
        [:td "4"]
        [:td "Philip Chaney"]
        [:td "$38,735"]
        [:td "Korea, South"]]]]]]])

(defn dashboard-ui []
  [:div
   [:div.row
    [get-more-space-card]
    [last-24h-revenue-card]
    [fixed-issues-card]
    [followers-card]]
   (comment) ; No charts for now
   [:div.row
    [daily-sales]
    [email-subscriptions]
    [completed-tasks]]
   [:div.row
    [tasks]
    [employees-stats]]])
