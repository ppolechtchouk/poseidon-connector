(ns poseidon-connector.db
  (:require [poseidon-connector.properties :as prop]
	    [poseidon-connector.frame :as frame]
	    [clojure.contrib.sql :as sql]
	    [clojure.contrib.loging :as log]
	    ))

(def *db* (prop/get-properties))

(defn store-frames
  "Takes frames from the queue and stores them in the database. If there are no frames, does nothing."
  []
  (when-let [frames (seq (take-while #(not (nil? %)) (repeatedly frame/pull)))]
    (sql/with-connection *db*
      (log/info (str "Connected to the " (:subname *db*) " DB"))
      (doseq [frame frames]
	(try
	  (sql/insert-records :frames frame)
	  (log/info (str "Frame from TID " (:field1 frame) " stored/n"))
	  (catch Exception e (log/error e)))))))