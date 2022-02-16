(ns html.archive
  (:require
    [html.base :as base]
    [html.post :as post]
    [utils :as utils]))

(defn entry [post]
  [:article
   (utils/full-date-tag (:date post))
   [:a.ml-2 {:href (post/href post)} (:title post)]])

(defn archive-list [{:keys [posts]}]
  [:ol.list-none.p-0
   (for [post (reverse (sort-by :date posts))
         :when (not (:preview post))]
     [:li (entry post)])])

(defn page [{:keys [posts]}]
  (base/base
    {:title "Archive"
     :content
     (list
       [:h1.mt-3 "Archive"]
       (archive-list {:posts posts}))}))

