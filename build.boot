
(set-env!
  ; where we put index.html
  :asset-paths #{"assets"}
  ; source code
  :resource-paths #{"src"}

  :dependencies '[[org.clojure/clojurescript "1.9.216"     :scope "test"]
                  [org.clojure/clojure       "1.8.0"       :scope "test"]
                  [adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                  [adzerk/boot-reload        "0.4.11"      :scope "test"]
                  [mvc-works/hsl             "0.1.2"]
                  [respo/ui                  "0.1.2"]
                  [respo                     "0.3.25"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]])

; development environment with hot code swapping
(deftask dev []
  (comp
    (watch)
    (reload :on-jsload 'minimal-respo.main/on-jsload!
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target)))

; build and minify code
(deftask build-advanced []
  (comp
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5})
    (target)))
