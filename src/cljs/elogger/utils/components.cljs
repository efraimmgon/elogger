(ns elogger.utils.components
  (:require
    cljs.pprint
    [clojure.string :as string]
    [cljs.reader :as reader]
    [elogger.utils.events :refer [<sub]]
    [elogger.utils.forms :as forms]
    [oops.core :as oops]
    [reagent.core :as r]
    [re-frame.core :as rf]))

;;; ---------------------------------------------------------------------------
;;; UTILS

(defn on-key-handler
  "Takes a map of .-key's and functions. Returns a matching function. If
  the event.key str is present in the map, it calls the respective function."
  [keymap]
  (fn [event]
    (when-let [f (get keymap
                      (oops/oget event "key"))]
      (f))))

; ------------------------------------------------------------------------------
; Debugging
; ------------------------------------------------------------------------------

(defn pretty-display [data]
  [:pre
   (with-out-str
    (cljs.pprint/pprint data))])


; ------------------------------------------------------------------------------
; Forms
; ------------------------------------------------------------------------------

(defn form-group
  "Bootstrap's `form-group` component."
  [label & input]
  [:div.form-group
   [:label label]
   (into
    [:div]
    input)])

(defn radio-input
  "Radio component, with common boilerplate."
  [{:keys [name class doc value label checked?]}]
  [:div.form-check.form-check-radio
   [:label.form-check-label
    [forms/input
     {:type :radio
      :name name
      :class (or class "form-check-input")
      :doc doc
      :value value
      :checked? checked?}]
    label
    [:span.circle>span.check]]])

(defn checkbox-input
  "Checkbox component, with common boilerplate."
  [{:keys [name class doc label checked?]}]
  [:div.form-check
   [:label.form-check-label
    [forms/input
     {:type :checkbox
      :name name
      :class (or class "form-check-input")
      :doc doc
      :checked? checked?}]
    label
    [:span.form-check-sign>span.check]]])


; ------------------------------------------------------------------------------
; MISC
; ------------------------------------------------------------------------------


(defn card [{:keys [attrs header title subtitle body content footer]}]
  [:div.card
   (if header 
     (update attrs :class str " card-nav-tabs")
     attrs)
   (when header [:div.card-header.card-header-primary header])
   [:div.card-body
    (when title [:h4.card-title title])
    (when subtitle [:h6.card-subtitle.text-muted subtitle])
    (when body [:div.card-text body])
    (when footer footer)]])   

(defn breadcrumbs [& items]
  (into
   [:ol.breadcrumb
    [:li [:a {:href "/"} "Home"]]]
   (for [{:keys [href title active?] :as item} items]
     (if active?
       [:li.active title]
       [:li [:a {:href href} title]]))))

(defn thead 
  ([headers] (thead nil headers))
  ([attrs headers]
   [:thead
    attrs
    [:tr
     (for [th headers]
       ^{:key th}
       [:th th])]]))

(defn tbody [rows]
  (into
   [:tbody]
   (for [row rows]
     (into
      [:tr]
      (for [td row]
        [:td td])))))

(defn thead-indexed
  "Coupled with `tbody-indexed`, allocates a col for the row's index."
  [headers]
  [:thead
   (into
     [:tr
      [:th "#"]]
     (for [th headers]
       [:th th]))])

(defn tbody-indexed
  "Coupled with `thead-indexed`, allocates a col for the row's index."
  [rows]
  (into
   [:tbody]
   (map-indexed
    (fn [i row]
      (into
       [:tr [:td (inc i)]]
       (for [td row]
         [:td
          td])))
    rows)))

(defn tabulate
  "Render data as a table.
  `rows` is expected to be a coll of maps.
  `ks` are the a the set of keys from rows we want displayed.
  `class` is css class to be aplied to the `table` element."
  ([ks rows] (tabulate ks rows {}))
  ([ks rows {:keys [class]}]
   [:table
    {:class class}
    ;; if there are extra headers we append them
    [thead (map (comp (fn [s] (string/replace s #"-" " "))
                      string/capitalize
                      name)
                ks)]
    [tbody (map (apply juxt ks) rows)]]))

; ------------------------------------------------------------------------------
; Modal
; ------------------------------------------------------------------------------
#_
(defn modal [{:keys [attrs header body footer]}]
  [:div attrs
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header
      [:div.modal-title
       [:h3 header]]]
     [:div.modal-body body]
     (when footer
       [:div.modal-footer
         footer])]]
   [:div.modal-backdrop.fade.in]])

(defn modal [{:keys [attrs header body footer]}]
  [:div
   (merge {:on-key-down
           (on-key-handler {"Escape" #(rf/dispatch [:remove-modal])})}
          attrs)
   [:div.modal-dialog
    [:div.modal-content
     (when header
       [:div.modal-header
        [:div.modal-title
         header]])
     (when body [:div.modal-body body])
     (when footer
       [:div.modal-footer
         footer])]]
   [:div.modal-backdrop
    {:on-click #(rf/dispatch [:remove-modal])}]])

; --------------------------------------------------------------------
; PAGER
; --------------------------------------------------------------------

(defn forward [i page-count]
  (if (< i (dec page-count)) (inc i) i))

(defn back [i]
  (if (pos? i) (dec i) i))

(defn nav-link [current-page i]
  [:li.page-item>a.page-link.btn.btn-primary
   {:on-click #(reset! current-page i)
    :class (when (= i @current-page) "active")}
   [:span i]])

(defn pager [page-count current-page]
  (when (> page-count 1)
    (into
     [:div.text-xs-center>ul.pagination.pagination-lg]
     (concat
      [[:li.page-item>a.page-link.btn
        {:on-click #(swap! current-page back page-count)
         :class (when (= @current-page 0) "disabled")}
        [:span "<<"]]]
      (map (partial nav-link current-page) (range page-count))
      [[:li.page-item>a.page-link.btn
        {:on-click #(swap! current-page forward page-count)
         :class (when (= @current-page (dec page-count)) "disabled")}
        [:span ">>"]]]))))

(comment
  (let [current-page (atom 0)
        page-count (atom [{:page 1} {:page 2}])]
    [pager (count @page-count) current-page]))