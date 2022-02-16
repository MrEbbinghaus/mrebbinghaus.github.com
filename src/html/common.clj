(ns html.common
  (:require
    [clojure.core :exclude [list]]))

(defn tag-badge [tag]
  [:span.badge.border-1
   {:class "text-slate-500 border-slate-200 dark:border-slate-800"}
   tag])

(defn tag-row [tags]
  [:span (map tag-badge tags)])

(defmulti entry :type)

(defn list [{:keys [posts]}]
  [:ol.list-none.divide-y.divide-opacity-10.p-0
   (for [post posts]
     [:li (entry post)])])

