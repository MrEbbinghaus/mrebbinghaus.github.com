(ns html.index
  (:require
    [html.base :as base]
    [hiccup.util :as hu]
    [utils :as utils]
    [html.common :as common]))

(defmethod common/entry :default
  [{:keys [date body] :as post}]
  (common/base post
    (utils/date-tag date)
    (hu/raw-string body)))

(defn page [{:keys [posts]}]
  (let [latest-posts
        (->> posts
          (remove :preview)
          (filter #(= :post (get % :type :post)))
          (sort-by :date)
          reverse
          #_(take 3))]
    (base/base
      {:title "Björn's Blog"
       :content
       (list
         [:h1.mt-3.sr-only "Last posts"]
         (common/list {:posts latest-posts}))})))
