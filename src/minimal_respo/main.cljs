
(ns minimal-respo.main
  (:require [respo.core :refer [render! clear-cache!]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

; states tree, make sure to use {}
(defonce ref-states (atom {}))

; where you put data
(defonce ref-store (atom 0))

; pure function to update store
(defn updater [store op op-data]
  ; use `case` to detect action types
  (case op
    :inc (+ store op-data)
    store))

; connect user actions to updater
(defn dispatch! [op op-data]
  ; use reset! and it triggers watchers
  (reset! ref-store (updater @ref-store op op-data)))

; component definitions

; event handler to call dispatch!
(defn on-click [e dispatch!]
  (dispatch! :inc 1))

; button renderer
(defn render-button []
  (fn [state mutate!]
    (div {:style ui/button
          ; event handler
          :event {:click on-click}}
      (comp-text "inc" nil))))

; button component
(def comp-button (create-comp :button render-button))

; container renderer
(defn render-container [store]
  (fn [state mutate!]
    (div {}
      ; insert text
      (comp-text store nil)
      ; reuse child component
      (comp-button))))

; container component
(def comp-container (create-comp :container render-container))

; mount and update components

; function to conntect respo.core/render!
(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render!
      ; render component tree into virtual DOM tree
      (comp-container @ref-store)
      target dispatch! ref-states)))

(defn -main! []
  (enable-console-print!)
  (render-app!)
  ; watch updates and do rerender
  (add-watch ref-store :changes render-app!)
  (add-watch ref-states :changes render-app!)
  (println "app started!"))

(set! (.-onload js/window) -main!)

; this function handles code updates
(defn on-jsload! []
  ; clear rendering caches of virtual DOM
  (clear-cache!)
  ; rerender with diff/patch without rendering caches of virtual DOM
  (render-app!)
  (println "code updated."))
