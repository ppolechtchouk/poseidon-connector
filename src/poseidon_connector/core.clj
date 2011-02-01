(ns poseidon-connector.core
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:require [poseidon-connector.network :as net]
	    [poseidon-connector.db :as db]
	    [poseidon-connector.properties :as prop]
	    [clojure.contrib.logging :as log]))



(defn shutdown
  "Use as the hook for Runtime.addShutdownHook to close all server and DB threads"
  []
  (do
    (log/info "Poseidon Connector shutting down...")
    (net/stop-all)
    (db/stop-db-dispatch)))


(defn -main [& args]
  (with-command-line args
    "Poseidon connector server. Use db.properties file to configure the db connection. CTRL-C in the console window to stop the server."
    [ [start? "Starts the connector server on the specified port"]
      [test? "Send a test message to the server on the specified port"]
      [port p "Server port" "7654"]
      remaining ]
    (cond
   ;  stop? (net/stop-server port)
     start? (do
	      (log/info "Poseidon Connector starting...")
	      (.addShutdownHook (Runtime/getRuntime) (Thread. shutdown))
	      (net/start-server (Integer/parseInt port))
	      (db/start-db-dispatch)
	      )
	      
     test? (net/test-message (Integer/parseInt port))
     :else (println "Use -help parameter to get extended help")
     )))
