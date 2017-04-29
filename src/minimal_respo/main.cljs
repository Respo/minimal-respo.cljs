
(ns minimal-respo.main
  (:require [respo.core :refer [render! clear-cache!]]
            [respo.cursor :refer [mutate with-cursor]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]
            [respo.comp.space :refer [comp-space]]))

; where you put data
(defonce ref-store
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
  (reset! ref-store (updater @ref-store op op-data)))

; component definitions

; event handler to call dispatch!
(defn on-click [e dispatch!]
  (dispatch! :inc 1))

; button component
(def comp-button
  (create-comp :button
    (fn []
      (fn [cursor]
        (div {:style ui/button
              ; event handler
              :event {:click on-click}}
          (comp-text "inc" nil))))))

; container component
(def comp-container
  (create-comp :container
    (fn container [store]
      (fn [cursor]
        (div {}
          ; insert text
          (comp-text (:point store) nil)
          ; some spaces
          (comp-space 8 nil)
          ; reuse child component
          (comp-button))))))

; mount and update components

; function to conntect respo.core/render!
(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render!
      ; render component tree into virtual DOM tree
      (comp-container @ref-store)
      target dispatch!)))

(defn -main! []
  (enable-console-print!)
  (render-app!)
  ; watch updates and do rerender
  (add-watch ref-store :changes render-app!)
  (println "app started!"))

(set! (.-onload js/window) -main!)

; this function handles code updates
(defn on-jsload! []
  ; clear rendering caches of virtual DOM
  (clear-cache!)
  ; rerender with diff/patch without rendering caches of virtual DOM
  (render-app!)
  (println "code updated."))
