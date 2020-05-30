(ns laconic-cms.test.handler
  (:require
    [clojure.test :refer :all]
    [ring.mock.request :as mock]
    [laconic-cms.handler :refer :all]
    [laconic-cms.middleware.formats :as formats]
    [muuntaja.core :as m]
    [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'laconic-cms.config/env
                 #'laconic-cms.handler/app-routes)
    (f)))

(deftest test-app
  (testing "main route"
    (let [response ((app) (mock/request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (mock/request :get "/invalid"))]
      (is (= 404 (:status response)))))
  
  (testing "user routes"
    (let [response ((app) (mock/request :get "/api/blog-posts"))])))
    

(comment
  (run-tests 'laconic-cms.test.handler)

  (-> (mock/request :get "/api/comments/1")
      ((app))))
      ;:status))
      ;:body
      ;parse-json
      ;first))