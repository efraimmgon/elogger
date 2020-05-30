(ns laconic-cms.db.pathom
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc :refer
     [defresolver]]
    [laconic-cms.db.resolvers :as resolvers]
    [laconic-cms.db.sql.common :as common]))

(defn mergev [colls]
  (reduce into [] colls))

(def registry
  (mergev [resolvers/blog-post-registry
           resolvers/like-registry
           resolvers/page-registry
           resolvers/threaded-comment-registry
           resolvers/user-registry]))

(defn custom-map-reader
  [{:keys [ast query] :as env}]
  (prn 'query query)
  (prn 'entity (p/entity env))
  (prn 'key (:key ast))
  (clojure.pprint/pprint (find (p/entity env) (:key ast)))
  (p/map-reader env))



(def parser*
  (p/parser
    {::p/env     {::p/reader               [p/map-reader
                                            pc/reader2
                                            pc/open-ident-reader
                                            p/env-placeholder-reader]
                  ::p/placeholder-prefixes #{">"}}
     ::p/mutate  pc/mutate
     ::p/plugins [(pc/connect-plugin {::pc/register registry})
                  p/error-handler-plugin
                  (p/post-process-parser-plugin p/elide-not-found)
                  p/trace-plugin]}))

(defn parser [query]
  (let [[ident cols] (first query)
        ret (parser* {} [query])
        result (get ret ident)]
    (when (seq result)
      result)))

(comment
  
  (parser {[:blog-posts/all] [{:blog-posts/list common/blog-post-query}]})
  (parser {[:blog-post/id 9] common/blog-post-query})
  
  
  (parser {[:page/id 10] common/page-columns})
  (parser {[:pages/all] [{:pages/list common/page-query}]})
  
  (parser {[:likes/id 1] common/like-columns})
  (-> (parser {[:likes/all] [{:likes/list common/like-query}]})
      :likes/list
      count)
  
  (parser {[:threaded-comment/id 23] common/comment-query})
  (parser {[:threaded-comments/all] [{:threaded-comments/list common/comment-query}]})
  
  
  (parser {[:users/all] [{:users/list common/user-query}]})
  
  :end)
