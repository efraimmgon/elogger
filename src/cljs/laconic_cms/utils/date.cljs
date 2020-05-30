(ns laconic-cms.utils.date)

(defn to-date-str [x]
  (.toDateString 
    (js/Date. x)))