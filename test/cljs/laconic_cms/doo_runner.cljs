(ns laconic-cms.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [laconic-cms.core-test]))

(doo-tests 'laconic-cms.core-test)

