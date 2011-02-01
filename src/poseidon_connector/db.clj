(ns poseidon-connector.db
  (:require [poseidon-connector.properties :as prop]
	    [poseidon-connector.frame :as frame]
	    [clojure.contrib.sql :as sql]
	    [clojure.contrib.logging :as log]
	    ))

(def *db* (prop/get-properties))

(defn remove-empty-fields
  "Returns a frame map with the fields that contain emty string removed"
  [frame]
  (into {}
	(remove (fn [[_ v]] (empty? v)) frame)))

(defn- store-frame*
  "Internal function. Stores a single frame into the DB. Must be used inside the with-connection sql macro."
  [frame]
  (try
    (sql/insert-records :poseidon.frames (remove-empty-fields frame)) ; only non-null values will be inserted
    (log/info (str "Frame from TID " (:field1 frame) " stored"))
    (catch Exception e (log/error e))))

(defn store
  "Stores a frame into the DB."
  [frame]
  (when frame
    (sql/with-connection *db*
      (log/info (str "Connected to the " (:subname *db*)))
      (store-frame* frame))))

(defn batch-store
  "Takes all available frames from the queue and stores them in the database. If there are no frames, does nothing."
  []
  (when-let [frames (seq (take-while (complement nil?) (repeatedly frame/pull)))]
    (sql/with-connection *db*
      (log/info (str "Connected to the " (:subname *db*) " DB"))
      (doseq [frame frames]
	(store-frame* frame)))))


(def *keep-running* (atom true)) ; checked by the db-dispatch
(defn db-dispatch
  "Periodically checks the frames queue and stores any frames present to the DB. Should be launched with start-db-dispatch"
  []
  (when @*keep-running*
    (batch-store)
    (try (Thread/sleep 3000))
    (recur)))

(defn start-db-dispatch
  "Launches db-dispatch in a separate thread"
  []
  (swap! *keep-running* (fn [_] true)) ; make sure that db-dispatch will run
  (.start (Thread. db-dispatch))
  (log/info "Started DB dispatch"))

(defn stop-db-dispatch
  "Notifies the db-dispatch that it should stop."
  []
  (swap! *keep-running* (fn [_] false))
  (log/info "Stopping DB dispatch..."))