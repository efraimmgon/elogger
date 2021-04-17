(ns elogger.db.seeds
  (:require
   [buddy.hashers :as hashers]
   [faker.name :as fname]
   [faker.internet :as finternet]
   [faker.lorem :as florem]
   [elogger.db.core :as db :refer [*db*]]
   [elogger.db.pathom :refer [parser]]
   [elogger.db.sql.common :as common]
   [elogger.db.sql.threaded-comment :as tcomment]
   [elogger.db.sql.user :refer 
    [create-user-with-profile! get-users]] 
   [elogger.db.sql.blog-post :as blog-post :refer [create-post!]]
   [elogger.db.sql.page :refer [create-page!]]
   [luminus-migrations.core :as migrations]))

; -----------------------------------------------------------------------------
; Helpers

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))


(defn now []
  (-> (java.util.Date.) .getTime java.sql.Timestamp.))

(defn rand-user-id []
  (some-> (get-users) 
          seq
          rand-nth 
          :users/id))

(defn rand-blog-post-with-comments-id []
  (->>
    (parser {[:blog-posts/all] [{:blog-posts/list common/blog-post-comment-query}]})
    :blog-posts/list
    (filter :blog-post/comments)
    rand-nth
    :blog-post/id))

(defn rand-blog-post-id []
  (some-> (blog-post/get-posts) seq rand-nth :blog-post/id))

(defn blog-post-comments-ids [blog-post-id]
  (->>
    (parser 
      {[:blog-post/id blog-post-id]
       [{:blog-post/comments [:threaded-comment/parent
                              :threaded-comment/id]}]})
    :blog-post/comments))

(defn maybe [x] (rand-nth [x nil]))

(def statuses ["published" "draft"])

(defn words [n]
  (->> (florem/words)
       (take n)
       (clojure.string/join " ")))

(defn paragraphs [n]
  (->> (florem/paragraphs)
       (take n)
       (clojure.string/join "\n\n")))

;;; ---------------------------------------------------------------------------
;;; Core

(defn create-users! []
  (println "Creating users")
  (dotimes [i 5]
    (let [profile (maybe {:profile/first-name (maybe (fname/first-name))
                          :profile/last-name (maybe (fname/last-name))
                          :profile/bio (maybe (words (rand-int 20)))})]
      (create-user-with-profile!
        (merge profile
               {:users/username (finternet/user-name)
                :users/email (finternet/email)
                :users/password (hashers/derive "foobar123")
                :users/is-active (boolean (maybe true))
                :users/admin (boolean (maybe true))})))))

(defn create-blog-posts! []
  (println "Creating blog posts")
  (dotimes [i 10]
    (let [user-id (:users/id (rand-nth (get-users)))
          params {:blog-post/author-id user-id
                  :blog-post/title (words 5)
                  :blog-post/subtitle (maybe (words 5))
                  :blog-post/content (paragraphs 5)
                  :blog-post/status (rand-nth statuses)}]
      (create-post! params))))

(defn create-comments! []
  (println "Creating comments")
  (dotimes [i 20]
    (let [user-id (:users/id (rand-nth (get-users)))
          blog-post-id (:blog-post/id (rand-nth (blog-post/get-posts)))
          root-comments (->> (parser 
                               {[:blog-post/id blog-post-id]
                                [{:blog-post/comments [:threaded-comment/parent
                                                       :threaded-comment/id]}]})
                             :blog-post/comments
                             (filter #(nil? (:threaded-comment/parent %))))
          parent-id  (when (seq root-comments)
                       (-> root-comments
                           rand-nth
                           :threaded-comment/id))
          ;; 50% chance we'll create a root comment or reply to a root comment.
          params {:blog-post/id blog-post-id
                  :threaded-comment/author-id user-id
                  :threaded-comment/title (words 5)
                  :threaded-comment/body (paragraphs 5)
                  :threaded-comment/parent (maybe parent-id)
                  :threaded-comment/is-approved (boolean (maybe true))
                  :threaded-comment/is-deleted (boolean (maybe true))}]
      (blog-post/create-comment!
       params))))

(defn create-pages! []
  (println "Creating pages")
  (dotimes [i 10]
    (let [user-id (:users/id (rand-nth (get-users)))]
      (create-page!
       {:page/author-id user-id
        :page/title (words 5)
        :page/subtitle (maybe (words 5))
        :page/content (paragraphs 5)
        :page/status (rand-nth statuses)}))))

;; todo: comment likes
;; todo: blog post likes
(defn create-likes! []
  
  (let [blog-post-id-seq (->> (parser {[:blog-posts/all] 
                                       [{:blog-posts/list [:blog-post/id]}]})
                              :blog-posts/list)
        comment-id-seq (->> (parser {[:threaded-comment/all]
                                     [{:threaded-comments/list
                                       [:threaded-comment/id]}]})
                            :threaded-comments/list)
        user-id-seq (->> (parser {[:users/all] [{:users/list [:users/id]}]})
                         :users/list)]
    ;;; A user should have, at most, 1 like per post or comment.
    (println "Creating likes for blog posts")
    (doseq [bpid blog-post-id-seq]
      (doseq [uid (-> user-id-seq 
                      count 
                      rand-int 
                      inc
                      (take user-id-seq))]
        (blog-post/like! 
          (merge bpid uid))))
    (println "Creating likes for comments")
    (doseq [cid comment-id-seq]
      (doseq [uid (-> user-id-seq count rand-int inc
                      (take user-id-seq))]
        (tcomment/like! 
          (merge cid uid))))))

(defn run []
  (reset-db)
  (create-users!)
  (create-blog-posts!)
  (create-pages!)
  (create-comments!)
  (create-likes!)
  (println "Done!"))
  
(comment
  (run))
