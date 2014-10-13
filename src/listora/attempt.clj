(ns listora.attempt
  (:require [clojure.core.async :as a :refer [<! go go-loop thread]]))

(def channel
  (a/chan (a/buffer 1024)))

(defn- process-attempt [{:keys [thunk return]}]
  (go (let [value (<! (thread (thunk)))]
        (return value))))

(defn- run-attempts []
  (go-loop []
    (when-let [attempt (<! channel)]
      (process-attempt attempt)
      (recur))))

(let [run (delay (run-attempts))]
  (defn startup-attempts []
    (deref run)))

(defn shutdown-attempts []
  (a/close! channel))

(defn attempt [thunk & [{:as options}]]
  (startup-attempts)
  (let [return (promise)]
    (a/put! channel {:thunk thunk, :return return})
    return))
