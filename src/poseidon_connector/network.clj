(ns poseidon-connector.network
  (:use [clojure.contrib server-socket str-utils]
	[clojure.contrib.io :exclude (spit)])
  (:require [clojure.contrib.logging :as log :only []]
	    [poseidon-connector.frame :as frame :only [push]])
  (:import [java.net Socket]
	   [java.io BufferedReader InputStreamReader OutputStreamWriter DataInputStream]))

(def *servers* (ref {})) ; map of the {port server} used for the stop-server

(defn from-big-endian
  "Converts 2 bytes in big endian order into an int"
  [b1 b2]
  (bit-or (bit-shift-left (int b1) 8) b2))

(defn read-frame
  "Reads a frame of data "
  [in out]
  (let [input (DataInputStream. in)
	length (.readUnsignedShort input) 
	content
	(apply str ; may need to use doall to realise the lazy- seq
	       (doall
		(take (- length 2)    ; 2 bytes have already been read
		      (repeatedly (fn []
				    (try (char (.readByte input))
					 (catch java.io.EOFException e nil)
					 (catch Exception e (log/error e))))))))
	frame {:length length
	       :content content
	       :timestamp (java.util.Date.)}
	]
    (log/info "Frame received.")
    (log/debug frame)
    (frame/push frame)
    ))

(defn echo
"An echo function for an echo server"
[in out]
(binding [*in* (BufferedReader. (InputStreamReader. in))
	  *out* (OutputStreamWriter. out)
	  ]
  (print "Welcome to the ECho Server\r\n")
  (loop []
    (let [input (read-line)]
      (print (str input "\r\n>"))
      (flush))
    (recur))))


(defn start-server
  "Starts the server on the specified port"
  [port]
  (let [server (create-server port read-frame)]
    (dosync alter *servers* assoc port server)
    (log/info (str "Poseidon connector server started on port " port "/n"))
    server))

(defn stop-server
  [port]
  "Stops the server on the specified port"
  (dosync
   (if-let [server (get @*servers* port)]
     ((alter *servers* dissoc port)
      (close-server server)
      (log/info (str "Server on port " port " stopped.")))
     (log/warning (str "No server running on port " port)))))

(defn stop-all
  "Stops all running servers."
  []
  (dosync
   (doseq [port (keys @*servers*)] (stop-server port))))

(defn echo-server
  "Starts the server on the specified port"
  [port]
  (create-server port echo))

(defn start-client
  "Creates a socket and connects it to the host and port. Returns a map :socket :in :out"
  ([host port]
     (let [socket (Socket. host port)
	   in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
	   out (OutputStreamWriter. (.getOutputStream socket))]
       {:socket socket :in in :out out}))
  ([port]
     (start-client nil port)))

(defn send-message
  "Creates a socket connection to the server, sends the message string and then closes the socket. if host is not given, localhost is assumed"
  [client msg]
  (binding [*out* (:out client)]
    (print msg)
    (flush)))

(def *test-frame* (str (char 1) "!65129405;00000000;000020;2043129405;091218;094557;90;2205;0;0;0;091218;094644000;0;0;999;0;0;04;0;0;0;0;0;0;0;;1;0;0;0088;1;0;;;;;07;$UTS1;10.100.103.23;DIAG;                                                                                                                                 "))

(defn test-message
  "Creates a socket on the specified port and sends the message specified by the*test-frame*. The socket is then closed."
  [port]
  (let [clt (start-client port)]
    (send-message clt *test-frame*)
    (.close (:socket clt))))
