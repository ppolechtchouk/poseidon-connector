(ns poseidon-connector.properties
  (:require [clojure.contrib.properties :as prop :only []]))

(def *properties-file* "db.properites")

(def *defaults*
  (sorted-map :classname "org.postgresql.Driver"
	      :subprotocol "postgresql"
	      :subname "//10.10.211.63/poseidon"
	      :user "poseidon"
	      :password "poseidon"))

;; creation of the initial properties file
(defn write-initial-properties
  "Creation of the initial properties file. For utility only."
  []
  (prop/write-properties *defaults* *properties-file*))

(defn get-properties
  "Loads the properties from a file specified by the *properties-file* and returns them as a map of {:property value}. If a particular property is not present in the file, one from *defaults* will be used."
  []
  (do
    (prop/read-properties *properties-file*)
    (apply conj
	   (map
	    (fn [k] {k (prop/get-system-property k (str (k *defaults*)))})
	    (keys *defaults*)))))