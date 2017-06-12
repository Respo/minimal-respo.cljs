
; defcomp is a macro, others are functions
(ns app.main
  (:require-macros [respo.macros :refer [defcomp]])
  (:require [respo.core :refer [render! clear-cache!]]
            [respo.cursor :refer [mutate with-cursor]]
            [respo-ui.style :as ui]
            [respo.alias :refer [div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]))

; where you put data
(defonce *store
  (atom
    {:point 0
     :states {}}))

; pure function to update store
(defn updater [store op op-data]
  ; use `case` to detect action types
  (case op
    :states (update store :states (mutate op-data))
    :inc (update store :point (fn [point] (+ point op-data)))
    store))

; connect user actions to updater
(defn dispatch! [op op-data]
  ; use reset! and it triggers watchers
  (reset! *store (updater @*store op op-data)))

; component definitions

; event handler to call dispatch!
(defn on-click [e dispatch!]
  (dispatch! :inc 1))

; button component, defined with a macro
(defcomp comp-button [text]
  (div {:style ui/button
        ; event handler
        :event {:click on-click}}
    (comp-text text nil)))

; container component
(defcomp comp-container [store]
  (div {}
    ; insert text
    (comp-text (:point store) nil)
    ; some spaces
    (comp-space 8 nil)
    ; calling child component, try a parameter
    (comp-button "inc")))

; mount and update components

; function to conntect respo.core/render!
(defn render-app! []
  (let [target (.querySelector js/document "#app")
        ; render component tree into virtual DOM tree
        app (comp-container @*store)]
    (render! app target dispatch!)))

(defn main! []
  (enable-console-print!)
  (render-app!)
  ; watch updates and do rerender
  (add-watch *store :changes render-app!)
  (println "app started!"))

(set! (.-onload js/window) main!)

; this function handles code updates
(defn reload! []
  ; clear rendering caches of virtual DOM
  (clear-cache!)
  ; rerender with diff/patch without rendering caches of virtual DOM
  (render-app!)
  (println "Code updated."))
