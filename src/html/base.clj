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

(def top-targets
  [["Home" "index.html"]
   ["Projects" "projects.html"]
   ["Publications" "publications.html"]
   ["Archive" "archive.html"]])

(defn header []
  [:header.relative
   [:div.max-w-screen-md.mx-auto.px-4.sm:px-6
    [:div.flex.justify-between.items-center.border-b-2.border-gray-100.py-2.md:justify-start.md:space-x-10
     [:nav.md:flex
      (for [[label href] top-targets]
        [:a.text-base.font-medium.text-slate-500.hover:text-black.dark:text-slate-300.dark:hover:text-white.md:p-4.p-2 {:href href} label])]]]])

(defn footer []
  [:footer.relative.h-16])

(defn body [{:keys [content]}]
  [:body.bg-white.subpixel-antialiased.dark:bg-gray-900.dark:text-white
   (header)
   [:div.p-0
    [:main.mx-auto.prose.prose-zinc.dark:prose-invert.px-4.sm:px-0
     content]]
   (footer)])

(defn base [{:keys [title meta content]}]
  [:html {:lang "en"}
   (head {:title title :meta meta})
   (body {:content content})])
  
