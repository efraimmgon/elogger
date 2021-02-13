(ns laconic-cms.db)


(def main-deps
  #{"material-kit-css"
    "demo-css"
    ; "bootstrap-material-design.min.js" ; common
    ; "moment.min.js" ; common
    ; "bootstrap-datetimepicker.js" ; common
    ; "nouislider.min.js" ; common
    "material-kit-js"})

(def admin-deps
  #{"material-dashboard-css"
    ; "bootstrap-material-design.min.js" ; common
    "perfect-scrollbar-js"
    ; "moment.min.js" ; common
    "sweetalert2-js"
    ; "jquery.validate.min.js" ; not needed. we have clj solutions
    ; "jquery.bootstrap-wizard.js" ; very simple in clj
    ; "bootstrap-datetimepicker.min.js" ; common
    ; "jquery.dataTable.min.js" ; don't need it for now
    ; "bootstrap-tagsinput.js" ; very simple in clj
    "jasny-bootstrap-js"
    ; "fullcalendar.min.js" ; not needed for now.
    ; "jquery.jvectormap.js" not needed for now.
    ; "nouislider.js" ; common
    ; "arrive.min.js" ; don't see what's the point on this
    ; "googlemaps.js"
    "chartist-js"
    ; "bootstrap-notify.js" ; fuck this shit
    "material-dashboard-js"})
    
(def default-db
  {:main/deps main-deps
   :admin/deps admin-deps})
