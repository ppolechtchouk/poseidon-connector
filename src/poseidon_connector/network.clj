(ns poseidon-connector.network
  (:use [clojure.contrib server-socket str-utils]
	[clojure.contrib.io :exclude (spit)])
  (:import [java.net Socket]
	   [java.io BufferedReader InputStreamReader OutputStreamWriter DataInputStream]))


(defn process-frame
  "Processes POSEIDON frame (as string). Splits it into 41 constituent fields"
  [#^String frame]
  (re-split #";" frame))

(def message (atom nil))

(defn reset-message []
  (swap! message (fn [_] nil)))

(def log (agent ()))

(defn from-big-endian
  "Converts 2 bytes in big endian order into an int"
  [b1 b2]
  (bit-or (bit-shift-left (int b1) 8) b2))



(defn read-frame
  "Reads a frame of data "
  [in out]
  (let [input (DataInputStream. in)
	length (- (.readUnsignedShort input) 2)
					;frame  (repeatedly length #(.readChar input))
	frame (repeatedly (fn []
		  (try (char (.readByte input))
		       (catch java.io.EOFException e nil))))
	]
    (swap! message (fn [_] (apply str (take length frame))))
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

(defn connector
  ""
  [input-stream output-stream]
  (binding [*in* (BufferedReader. (InputStreamReader. input-stream))]    
    ())
  ;TODO(
  )

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

