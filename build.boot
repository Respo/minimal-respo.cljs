
(set-env!
  ; where we put index.html
  :asset-paths #{"assets"}
  ; source code
  :resource-paths #{"src"}

  :dependencies '[[org.clojure/clojure       "1.8.0"       :scope "test"]
                  [org.clojure/clojurescript "1.9.293"     :scope "test"]
                  [adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                  [adzerk/boot-reload        "0.4.13"      :scope "test"]
                  [mvc-works/hsl             "0.1.2"]
                  [respo/ui                  "0.1.5"]
                  [respo                     "0.3.32"]])

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

; build and optimize code
(deftask build-advanced []
  (comp
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5})
    (target)))
