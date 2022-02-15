(ns html.projects
  (:require
    [html.base :as base]
    [html.common :as common]
    [utils :as utils]))

(defmethod common/entry :project [{:keys [date project/state] :as post}]
  (common/base post
    (when date
      (utils/date-tag date))
    (when state
      [:p "Status:"
       (case state
         :active "Active"
         "")])))

(defn page [{:keys [posts]}]
  (let [projects
        (->> posts
          (remove :preview)
          (filter #(= :project (:type %)))
          (sort-by :date)
          reverse)]
    (base/base
      {:title "Björn's Projects"
       :content
       (list
         [:h1.mt-3 "My Projects"]
         (common/list {:posts projects}))})))