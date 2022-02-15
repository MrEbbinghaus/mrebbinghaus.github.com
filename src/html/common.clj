(ns html.common
  (:require
    [clojure.core :exclude [list]]
    [hiccup.util :as hu]
    [html.post :as post]))

(defmacro base [post & body]
  `[:article
    [:h2 [:a {:href (post/href ~post)} (:title ~post)]]
    ~@body])

(defmulti entry :type)
(defmethod entry :default
  [{:keys [date body] :as post}]
  (base post
    (utils/date-tag date)
    (hu/raw-string body)))

(defn list [{:keys [posts]}]
  [:ol.list-none.divide-y.divide-opacity-10.p-0
   (for [post posts]
     [:li (entry post)])])

