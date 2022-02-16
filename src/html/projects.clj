(ns html.projects
  (:require
    [html.base :as base]
    [html.common :as common]
    [utils :as utils]
    [html.post :as post]))

(defmethod common/entry :project
  [{:keys [title date project/status tags] :as post}]
  [:article
   [:h2 [:a {:href (post/href post)} title]]
   (utils/full-date-tag date)
   (common/tag-row tags)
   (when status
     [:p "Status: "
      (case status
        :active "Active"
        "")])])

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