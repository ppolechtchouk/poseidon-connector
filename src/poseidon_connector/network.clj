(ns poseidon-connector.network
  (:use [clojure.contrib server-socket str-utils]
	[clojure.contrib.io :exclude (spit)])
  (:require [clojure.contrib.logging :as logger :only []]
	    [poseidon-connector.frame :as frame :only [push]])
  (:import [java.net Socket]
	   [java.io BufferedReader InputStreamReader OutputStreamWriter DataInputStream]))




(def message (atom nil))

(defn reset-message []
  (swap! message (fn [_] nil)))



(defn from-big-endian
  "Converts 2 bytes in big endian order into an int"
  [b1 b2]
  (bit-or (bit-shift-left (int b1) 8) b2))



(defn read-frame
  "Reads a frame of data "
  [in out]
  (let [input (DataInputStream. in)
	length (.readUnsignedShort input) 
				
	content (apply str ; may need to use doall to realise the lazy- seq
	       (take (- length 2) ; 2 bytes have already been read
		     (repeatedly (fn []
			      (try (char (.readByte input))
				   (catch java.io.EOFException e nil)
				   (catch Exception e (logger/error e)))))))
	frame {:length length
	       :content content
	       :timestamp (java.util.Date.)}
	]
    (logger/info "Frame received.")
    (logger/debug frame)
    (frame/push frame)
    ))

(defn echo
"An echo function for an echo server"
[in out]
(binding [*in* (BufferedReader. (InputStreamReader. in))
	 ; *out* (OutputStreamWriter. out)
	  ]
  (print "Welcome to the ECho Server\r\n")
  (loop []
    (let [input (read-line)]
      (swap! message (fn [_] input))
      (print (str input "\r\n>"))
      (flush))
    (recur))))


(defn start-server
  "Starts the server on the specified port"
  [port]
  (create-server port read-frame))

(defn stop
  [serv]
  "Stops the server"
  (close-server serv))

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
