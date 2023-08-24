(ns render
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [markdown.core :as md]
   [clojure.java.shell :refer [sh]]
   [babashka.curl :as curl]
   [clojure.data.xml :as xml]
   [hiccup2.core :as h]
   [html.post :as post]
   [html.archive :as archive]
   [html.index :as index]
   [html.publications :as publications]
   [html.projects :as projects]
   [utils :as utils]))


(def posts (->> "posts.edn"
                slurp
                (format "[%s]")
                edn/read-string
                (sort-by :date)
                reverse))

(def out-dir "public")

(when-not (fs/exists? out-dir)
  (fs/create-dir out-dir))


;;;; Sync images and CSS

(defn copy-assets! []
  (let [assets (fs/file "assets")]
    (when (fs/directory? assets)
      (println "Copy assets")
      (let [asset-out-dir (fs/create-dirs (fs/file out-dir "assets"))]
        (fs/copy-tree "assets" asset-out-dir {:replace-existing true})))))

(copy-assets!)

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

(doseq [{:keys [file local-render?]
         :as post}
        posts]
  (let [cache-file (fs/file ".work" (html-file file))
        markdown-file (fs/file "posts" file)
        stale? (seq (fs/modified-since cache-file
                                       [markdown-file
                                        "posts.edn"
                                        "templates"
                                        "src/render.clj"
                                        "src/html/"]))
        body (if stale?
               (let [body (or
                            (and (not local-render?)
                              (remote-markdown->html markdown-file))
                            (markdown->html markdown-file))]
                 (spit cache-file body)
                 body)
               (slurp cache-file))
        _ (swap! bodies assoc file body)
        html (h/html {} (post/page (assoc post :body body)))

        html-file (str/replace file ".md" ".html")]
    (fs/copy markdown-file (fs/file out-dir file) {:replace-existing true})
    (spit (fs/file out-dir html-file) (str html))))

(def posts-with-body
  (for [post-entry posts]
    (assoc post-entry :body (get @bodies (:file post-entry)))))

;;;; Generate static pages
(def static-pages
  {"index.html" index/page
   "archive.html" archive/page
   "projects.html" projects/page
   "publications.html" publications/page})

(defn index! []
  (doseq [[html-file render-fn] static-pages]
    (print "Render" html-file "... ")
    (spit (fs/file out-dir html-file)
      (h/html {} (render-fn {:posts posts-with-body})))
    (println "Done")))


;;;; Generate atom feeds

(xml/alias-uri 'atom "http://www.w3.org/2005/Atom")

(def blog-root "https://blog.ebbinghaus.me/")

(defn atom-feed
  ;; validate at https://validator.w3.org/feed/check.cgi
  [posts]
  (-> (xml/sexp-as-element
       [::atom/feed
        {:xmlns "http://www.w3.org/2005/Atom"}
        [::atom/title "Björn's Blog"]
        [::atom/link {:href (str blog-root "atom.xml") :rel "self"}]
        [::atom/link {:href blog-root}]
        [::atom/updated (utils/rfc-3339-now)]
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
           [::atom/published (utils/rfc-3339 date)]
           [::atom/updated (utils/rfc-3339 date)]
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
