(ns html.archive
  (:require
    [html.base :as base]
    [html.post :as post]
    [utils :as utils]))

(defn entry [post]
  [:article
   [:a {:href (post/href post)} (:title post)]
   " - "
   (utils/date-tag (:date post))])

(defn archive-list [{:keys [posts]}]
  [:ol
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

