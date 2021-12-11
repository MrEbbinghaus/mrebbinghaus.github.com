(ns render
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [markdown.core :as md]
   [selmer.parser :as selmer]
   [clojure.java.shell :refer [sh]]
   [babashka.curl :as curl]
   [clojure.data.xml :as xml]))


(def posts (sort-by :date (comp - compare)
                    (edn/read-string (format "[%s]"
                                             (slurp "posts.edn")))))

(def out-dir "public")

(when-not (fs/exists? out-dir)
  (fs/create-dir out-dir))


(selmer/set-resource-path! "templates/")
(selmer.parser/cache-off!)

;;;; Sync images and CSS
(let [assets (fs/file "assets")]
  (when (fs/directory? assets)
    (println "Copy assets")
    (let [asset-out-dir (fs/create-dirs (fs/file out-dir "assets"))]
      (fs/copy-tree "assets" asset-out-dir {:replace-existing true}))))


(defn compile-css! []
  (print "Compile CSS... ")
  (let [{:keys [exit out err]} (sh "npx" "tailwindcss" "-i" "templates/base.css" "-c" "tailwind.config.js")]
    (if (zero? exit)
      (do
        (println "Done")
        (spit (fs/file out-dir "base.css") out))
      (println "ERROR\n" err))))

;;;; Generate posts from markdown

(defn markdown->html [file]
  (let [_ (println "Processing markdown for file:" (str file))
        markdown (slurp file)
        ;; make links without markup clickable
        markdown (str/replace markdown #"http[A-Za-z0-9/:.=#?_-]+([\s])"
                              (fn [[match ws]]
                                (format "[%s](%s)%s"
                                        (str/trim match)
                                        (str/trim match)
                                        ws)))
        ;; allow links with markup over multiple lines
        markdown (str/replace markdown #"\[[^\]]+\n"
                              (fn [match]
                                (str/replace match "\n" "$$RET$$")))
        html (md/md-to-html-string markdown)
        html (str/replace html "$$RET$$" "\n")]
    html))


(def api-size-limit 400000) ; 400kB
(defn remote-markdown->html [path]
  (println "Fetching markdown for file:" (str path))
  (let [file (fs/file path)]
    (when
     (and (fs/regular-file? file)
          (< (fs/size file) api-size-limit))
      (let [{:keys [status headers body]}
            (curl/post "https://api.github.com/markdown/raw"
                       {:headers {"Accept" "application/vnd.github.v3+json"
                                  "Content-Type" "text/x-markdown"}
                        :body file})]
        (when-let [requests-remaining (get headers "x-ratelimit-remaining")]
          (println "Requests left:" requests-remaining))
        (when-let [limit-reset (get headers "x-ratelimit-reset")]
          (printf "Resets in %d minutes\n"
                  (quot (- (parse-long limit-reset) (quot (System/currentTimeMillis) 1000)) 60)))
        (when (= 200 status)
          body)))))

;; re-used when generating atom.xml
(def bodies (atom {}))

(defn html-file [file]
  (str/replace file ".md" ".html"))

(fs/create-dirs (fs/file ".work"))

(doseq [{:keys [file template]
         :or {template "post.html"}
         :as post}
        posts]
  (let [cache-file (fs/file ".work" (html-file file))
        markdown-file (fs/file "posts" file)
        stale? (seq (fs/modified-since cache-file
                                       [markdown-file
                                        "posts.edn"
                                        "templates"
                                        "render.clj"
                                        "tailwind.config.js"
                                        "highlighter.clj"]))
        body (if stale?
               (let [body (or (remote-markdown->html markdown-file)
                              (markdown->html markdown-file))]
                 (spit cache-file body)
                 body)
               (slurp cache-file))
        _ (swap! bodies assoc file body)
        html (selmer/render-file template (assoc post :body body))

        html-file (str/replace file ".md" ".html")]
    (spit (fs/file out-dir html-file) html)))

;;;; Generate archive page

(defn post-links []
  (reverse
   (sort-by
    :date
    (for [{:keys [file title date preview]} posts
          :when (not preview)]
      {:title title
       :date date
       :href (str/replace file ".md" ".html")}))))

(spit (fs/file out-dir "archive.html")
      (selmer/render-file "archive.html"
                          {:title "Archive"
                           :posts (post-links)}))


;;;; Generate index page with last 3 posts
(defn last-posts
  ([] (last-posts 3))
  ([n]
   (for [{:keys [file preview] :as post} (take n posts)
         :when (not preview)]
     (-> post
         (assoc :href (str/replace file ".md" ".html")
                :body (get @bodies file))))))

(defn index! []
  (print "Render Index... ")
  (spit (fs/file out-dir "index.html")
        (selmer/render-file "index.html"
                            {:posts (last-posts)}))
  (println "Done"))

;;;; Generate atom feeds

(.toInstant #inst "2021")
;; => #object[java.time.Instant 0x3e093177 "2021-01-01T00:00:00Z"]

;; => false



(xml/alias-uri 'atom "http://www.w3.org/2005/Atom")
(import java.time.Instant)

(defn rfc-3339-now []
  (str (Instant/now)))

(defn rfc-3339 [date]
  (str (.toInstant date)))

(def blog-root "https://blog.ebbinghaus.me/")

(defn atom-feed
  ;; validate at https://validator.w3.org/feed/check.cgi
  [posts]
  (-> (xml/sexp-as-element
       [::atom/feed
        {:xmlns "http://www.w3.org/2005/Atom"}
        [::atom/title "REPL adventures"]
        [::atom/link {:href (str blog-root "atom.xml") :rel "self"}]
        [::atom/link {:href blog-root}]
        [::atom/updated (rfc-3339-now)]
        [::atom/id blog-root]
        [::atom/author
         [::atom/name "Björn Ebbinghaus"]]
        (for [{:keys [title tags description date file preview]} posts
              :when (not preview)
              :let [html (str/replace file ".md" ".html")
                    link (str blog-root html)]]
          [::atom/entry
           [::atom/id link]
           [::atom/link {:href link}]
           [::atom/title title]
           (when description
             [::atom/summary description])

           (for [tag tags]
             [::atom/category
              {:term (name tag)
               :label (name tag)}])
           [::atom/published (rfc-3339 date)]
           [::atom/updated (rfc-3339 date)]
           [::atom/content {:type "html"}
            [:-cdata (get @bodies file)]]])])
      xml/indent-str))

(spit (fs/file out-dir "atom.xml") (atom-feed posts))


(index!)
;; Compile tailwind css. Needs to come after all HTML files are generated!
(compile-css!)

;; for JVM Clojure:
(defn -main [& _args]
  (System/exit 0))
