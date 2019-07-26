
(ns app.main
  (:require [respo.core :refer [defcomp <> div span render! clear-cache!]]
            [respo.cursor :refer [mutate]]
            [respo-ui.core :as ui]
            [respo.comp.space :refer [=<]]))

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
  (swap! *store updater op op-data))

; component definitions

; event handler to call dispatch!
(defn on-click [e dispatch! mutate!]
  (dispatch! :inc 1))

; button component, defined with a macro
(defcomp comp-button [text]
  (div {:style ui/button
        ; event handler
        :on-click on-click}
    (<> text)))

; container component
(defcomp comp-container [store]
  (div {}
    ; insert text
    (<> (:point store))
    ; some spaces, actually (comp-space 8 nil)
    (=< 8 nil)
    ; calling child component, try a parameter
    (comp-button "inc")))

; mount and update components

; function to conntect respo.core/render!
(defn render-app! []
  (let [target (.querySelector js/document "#app")
        ; render component tree into virtual DOM tree
        app (comp-container @*store)]
    (render! target app dispatch!)))

(defn main! []
  (render-app!)
  ; watch updates and do rerender
  (add-watch *store :changes render-app!)
  (println "App started!"))

; this function handles code updates
(defn reload! []
  ; clear rendering caches of virtual DOM
  (clear-cache!)
  ; rerender with diff/patch without rendering caches of virtual DOM
  (render-app!)
  (println "Code updated."))
