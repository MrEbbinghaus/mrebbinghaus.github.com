(ns html.publications
  (:require
    [html.base :as base]
    [utils :as utils]
    [html.common :as common]
    [html.post :as post]))

(defmethod common/entry :publication
  [{:keys [title date tags abstract] :as post}]
  [:article
   [:h2 [:a {:href (post/href post)} title]]
   [:aside
    (utils/month-tag date)
    (common/tag-row tags)]
   (when abstract [:p abstract])])

(defn page [{:keys [posts]}]
  (let [publications
        (->> posts
          (remove :preview)
          (filter #(#{:publication} (:type %)))
          (sort-by :date)
          reverse)]
    (base/base
      {:title "Björn's Publications"
       :content
       (list
         [:h1.mt-3 "My Publications"]
         (common/list {:posts publications}))})))