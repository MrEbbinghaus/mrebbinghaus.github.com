(ns html.publications
  (:require
    [html.base :as base]
    [html.post :as post]
    [utils :as utils]))

(defn entry [{:keys [title date] :as post}]
  [:article
   [:h2 [:a {:href (post/href post)} title]]
   (utils/month-tag date)])

(defn index-list [{:keys [posts]}]
  [:ol.list-none.divide-y.divide-opacity-10.p-0
   (for [post posts]
     [:li (entry post)])])

(defn page [{:keys [posts]}]
  (let [publications
        (->> posts
          (remove :preview)
          (filter #(= :publication (:type %)))
          (sort-by :date)
          reverse)]
    (base/base
      {:title "Björn's Publications"
       :content
       (list
         [:h1.mt-3 "My Publications"]
         (index-list {:posts publications}))})))