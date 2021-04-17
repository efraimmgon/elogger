(ns elogger.db.common)

(defmulti q 
  "Multimethod to dispatch the db queries."
  (fn 
    ([k] k)
    ([k params] k)))
