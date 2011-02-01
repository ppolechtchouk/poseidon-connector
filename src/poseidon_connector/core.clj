(ns poseidon-connector.core
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:require [poseidon-connector.network :as net]
	    [poseidon-connector.properties :as prop]
	    [clojure.contrib.logging :as log]))



(defn shutdown
  "Use as the hook for Runtime.addShutdownHook to close all server and DB threads"
  []
  (do
    (log/info "Poseidon Connector shutting down...")
    (net/stop-all))
  ;TODO DB
  )
(defn start
  "Starting the connector on the specified port"
  [port]
  )

(defn -main [& args]
  (with-command-line args
    "Poseidon connector server. Use db.properties file to configure the db connection."
    [ [start? "Starts the connector server on the specified port"]
      [stop?  "Sends the shutdown command to the connector server on the specified port. TODO"]
      [stopall? "Stops all running servers.TODO"]
      [test? "Send a test message to the server on the specified port"]
      [port p "Server port" 7654]
      remaining ]
    (cond
   ;  stop? (net/stop-server port)
     start? (do
	      (log/info "Poseidon Connector starting...")
	      (.addShutdownHook (Runtime/getRuntime) (Thread. shutdown))
	      (net/start-server (Integer/parseInt port)))
     test? (net/test-message (Integer/pareseInt port))
     :else (println "Use -help parameter to get extended help")
     )))
