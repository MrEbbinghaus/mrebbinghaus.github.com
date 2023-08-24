(require '[babashka.pods :as pods])
(require '[render :as render])

(pods/load-pod 'org.babashka/filewatcher "0.0.1")

(require '[pod.babashka.filewatcher :as fw])

(fw/watch "posts"
          (fn [_]
            (println "Re-rendering")
            (load-file "src/render.clj")))

(fw/watch "templates"
          (fn [_]
            (println "Re-rendering")
            (load-file "src/render.clj")))

(fw/watch "src"
          (fn [_]
            (println "Code changed. ")
            (load-file "src/render.clj")))

(fw/watch "assets"
          (fn [_]
            (println "Assets changed")
            (render/copy-assets!)))

@(promise)
