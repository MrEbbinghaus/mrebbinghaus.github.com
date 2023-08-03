(ns html.post
  (:require
    [html.base :refer [base]]
    [clojure.data.json :as json]
    [hiccup.util :as hu]
    [utils :as utils]
    [clojure.string :as str]
    [html.common :as common]))

(defn href [{:keys [file]}]
  (str/replace file ".md" ".html"))

(defn post-meta [{:keys [title date]}]
  (list 
   [:meta {:property "og:title" :content title}]
   [:meta {:property "og:type" :content "article"}]
   [:script {:type "application/ld+json"}
    (hu/raw-string
     (json/write-str
      {"@context" "https://schema.org"
       "@type" "BlogPosting"
       :headline (utils/abbreviate title)
       :datePublished (utils/rfc-3339 date)
       :author {"@type" "Person"
               :name "Björn Ebbinghaus"}}))]))


(defn page [{:keys [title body tags skip-archive?] :as post
             :or {skip-archive? false}}]
  (base
    {:title title
     :meta (post-meta post)
     :content
     (list
       [:article.pt-4.md:pt-8
        {:itemscope true
         :itemtype "https://schema.org/BlogPosting"}
        [:h1 {:itemprop "name"} title]
        (utils/full-date-tag (:date post))
        (common/tag-row tags)

        [:div {:itemprop "description articleBody"}
         (hu/raw-string body)]]
       [:footer {}
        (when-not skip-archive?
          [:div.object-right
           [:a.page-link {:href "archive.html"} "Archive"]])])}))