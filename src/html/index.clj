(ns html.index
  (:require
    [html.base :as base]
    [hiccup.util :as hu]
    [utils :as utils]
    [html.common :as common]
    [html.post :as post]))

(defmethod common/entry :default
  [{:keys [title date tags body abstract] :as post}]
  [:article
   [:h2 [:a {:href (post/href post)} title]]
   [:aside
    (utils/full-date-tag date)
    (common/tag-row tags)]
   (if abstract
     [:p abstract]
     (hu/raw-string body))])

(defn page [{:keys [posts]}]
  (let [latest-posts
        (->> posts
          (remove :preview)
          (filter #(#{:post :publication} (get % :type :post)))
          (sort-by :date)
          reverse
          #_(take 3))]
    (base/base
      {:title "Björn Ebbinghaus"
       :content
       (list
         [:h1.mt-3.sr-only "Latest posts"]
         (common/list {:posts latest-posts}))})))
