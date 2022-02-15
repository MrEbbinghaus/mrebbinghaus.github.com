(ns html.index
  (:require
    [html.base :as base]
    [html.post :as post]
    [hiccup.util :as hu]))

(defn entry [{:keys [title date body] :as post}]
  [:article
   [:h2 [:a {:href (post/href post)} title]]
   (post/date-tag date)
   (hu/raw-string body)])

(defn index-list [{:keys [posts]}]
  [:ol.list-none.divide-y.divide-opacity-10.p-0
   (for [post posts]
     [:li (entry post)])])

(defn page [{:keys [posts]}]
  (let [latest-posts
        (->> posts
          (remove :preview)
          (sort-by :date)
          reverse
          (take 3))]
    (base/base
      {:title "Björn's Blog"
       :content
       (list
         [:h1.mt-3.sr-only "Last posts"]
         (index-list {:posts latest-posts}))})))
