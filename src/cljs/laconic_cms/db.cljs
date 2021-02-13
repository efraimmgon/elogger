(ns laconic-cms.db)

#_
(def main-deps
  #{"bootstrap.css"
    "gsdk.css"
    "demo.css"
    "font-awesome.css"
    "open-sans.css"
    "pe-icon-7-stroke.css"
    "jquery-ui.js"
    "bootstrap.js"
    "gsdk-checkbox.js"
    "gsdk-morphing.js"
    "gsdk-radio.js"
    "gsdk-bootstrapswitch.js"
    "bootstrap-select.js"
    "bootstrap-datepicker.js"
    "chartist.js"})

(def main-deps
  #{"material-kit.css"
    "demo.css"
    ; "bootstrap-material-design.min.js" ; common
    ; "moment.min.js" ; common
    ; "bootstrap-datetimepicker.js" ; common
    ; "nouislider.min.js" ; common
    "material-kit.js"})

(def admin-deps
  #{"material-dashboard.css"
    ; "bootstrap-material-design.min.js" ; common
    "perfect-scrollbar.jquery.min.js"
    ; "moment.min.js" ; common
    "sweetalert2.js"
    ; "jquery.validate.min.js" ; not needed. we have clj solutions
    ; "jquery.bootstrap-wizard.js" ; very simple in clj
    ; "bootstrap-datetimepicker.min.js" ; common
    ; "jquery.dataTable.min.js" ; don't need it for now
    ; "bootstrap-tagsinput.js" ; very simple in clj
    "jasny-bootstrap.min.js"
    ; "fullcalendar.min.js" ; not needed for now.
    ; "jquery.jvectormap.js" not needed for now.
    ; "nouislider.js" ; common
    ; "arrive.min.js" ; don't see what's the point on this
    ; "googlemaps.js"
    "chartist.min.js"
    ; "bootstrap-notify.js" ; fuck this shit
    "material-dashboard.js"})
    
(def default-db
  {:main/deps main-deps
   :admin/deps admin-deps})
