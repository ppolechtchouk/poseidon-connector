(ns poseidon-connector.core
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:require [poseidon-connector.network :as net])
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
     start? (net/start-server port)
     test? (net/test-message port)
     )))
