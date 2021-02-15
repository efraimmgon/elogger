(ns elogger.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [elogger.core-test]))

(doo-tests 'elogger.core-test)

