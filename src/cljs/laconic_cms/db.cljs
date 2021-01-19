(ns laconic-cms.db)

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
    

(def default-db
  {:main/deps main-deps
   :admin/deps #{"material-dashboard-css"}}) 
