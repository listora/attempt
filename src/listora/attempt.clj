(ns listora.attempt
  (:require [clojure.core.async :as a :refer [<! go go-loop thread]]))

(def channel
  (a/chan (a/buffer 1024)))

(defn- run-thunk [thunk]
  (try {:return (thunk)}
       (catch Exception e {:error e})))

(defn- process-attempt [{:keys [thunk return fallback]}]
  (go (let [result (<! (thread (run-thunk thunk)))]
        (if-let [ret (:return result)]
          (return ret)
          (return fallback)))))

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

(def default-options
  {:fallback nil})

(defn attempt [thunk & [{:as options}]]
  (startup-attempts)
  (let [options (merge default-options options)
        return  (promise)]
    (a/put! channel (assoc options :thunk thunk, :return return))
    return))
