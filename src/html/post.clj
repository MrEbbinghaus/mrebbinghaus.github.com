(ns html.post
  (:require
   [html.base :refer [base]]
   [clojure.data.json :as json]
   [hiccup.util :as hu]
   [utils :as utils]
   [clojure.string :as str]
   [html.common :as common]))


(defn author-json-ld [author]
  (if (string? author)
    {"@type" "Person"
     :name author}
    (assoc author "@type" "Person")))


(def type->schmema-org-type
  {:publication "Article" ; Google doesn't understand ScholarlyArticle right now.
   :project "Article"
   nil "BlogPosting"})


(defn json-ld [{:keys [type title date authors tags abstract doi]
                :or {authors ["Björn Ebbinghaus"]}}]
  (cond->
   {"@context" "https://schema.org"
    "@type" (type->schmema-org-type type)
    :headline (utils/abbreviate title)
    :author (mapv author-json-ld authors)}
    (= :publication type) (assoc :additionalType "ScholarlyArticle")
    date (assoc :datePublished (utils/rfc-3339 date))
    tags (assoc :keywords tags)
    abstract (assoc :abstract abstract)
    doi (assoc :sameAs doi)))


(defn json-ld-tag [post]
  [:script {:type "application/ld+json"}
   (-> post json-ld json/write-str hu/raw-string)])


(defn href [{:keys [file]}]
  (str/replace file ".md" ".html"))


(defn footer [{:keys [skip-archive?] :or {skip-archive? false}}]
  [:footer {}
   (when-not skip-archive?
     [:div.object-right
      [:a.page-link {:href "archive.html"} "Archive"]])])


(defn post-meta [{:keys [title] :as post}]
  (list
   [:meta {:property "og:title" :content title}]
   [:meta {:property "og:type" :content "article"}]
   (json-ld-tag post)))


(defn page [{:keys [title body tags] :as post}]
  (base
   {:title title
    :meta (post-meta post)
    :content
    (list
     [:article.pt-4.md:pt-8
      [:h1 {:itemprop "name"} title]
      (utils/full-date-tag (:date post))
      (common/tag-row tags)

      (hu/raw-string body)]
     (footer post))}))