(ns html.publications
  (:require
    [html.base :as base]
    [utils :as utils]
    [html.common :as common]))

(defmethod common/entry :publication [{:keys [date] :as post}]
  (common/base post
    (utils/month-tag date)))

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
         (common/list {:posts publications}))})))