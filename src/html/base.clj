(ns html.base)

(defn head [{:keys [title meta]}]
  (apply vector 
         :head
         (concat
          [[:title title]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
           [:meta {:charset "UTF-8"}]
           [:link {:type "application/atom+xml" :rel "alternate" :href "atom.xml"}]
           [:link {:rel "stylesheet" :href "base.css"}]]
          meta)))

(defn header []
  [:header.relative
   [:div.max-w-screen-md.mx-auto.px-4.sm:px-6
    [:div.flex.justify-between.items-center.border-b-2.border-gray-100.py-2.md:justify-start.md:space-x-10
     [:nav.md:flex
      [:a.text-base.font-medium.text-gray-500.hover:text-gray-900.p-4 {:href "index.html"} "Home"]
      [:a.text-base.font-medium.text-gray-500.hover:text-gray-900.p-4 {:href "archive.html"} "Archive"]]]]])

(defn body [{:keys [content]}]
  [:body.bg-white.antialiased.dark:bg-gray-800.dark:text-white
   (header)
   [:div.p-0
    [:main.mx-auto.prose.dark:prose-invert.px-4.sm:px-0
     content]]])

(defn base [{:keys [title meta content]}]
  [:html {:lang "en"}
   (head {:title title :meta meta})
   (body {:content content})])
  
