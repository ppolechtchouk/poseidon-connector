(ns poseidon-connector.frame
  (:require [clojure.contrib.logging :as logger]
	    [clojure.contrib.str-utils :as string]))

(def *frames* (ref clojure.lang.PersistentQueue/EMPTY))

; Frame format {:length total_bytes :content message :timestamp datetime_recieved}

(defn process-content
  "Processes POSEIDON frame (as string). Splits it into constituent fields."
  [#^String content]
  (apply conj (map (fn [x y]  {(keyword (str "field" x)) y})
	(iterate inc 1)
	(string/re-split #";" content))))

(defn valid-length?
  "Returns true if the expected length of the frame is equal to the actual length. If length does not match, error is logged and false is returned."
  [{:keys [length content]}]
  (if (= (count content) (- length 2))
    true
    (do
      (logger/error
       (str "Invalid frame: expected length "
	    length
	    " actual length " (+ (count content) 2)))
      false)))

(defn valid-fields?
  "Returns true if the number of fields in the frame is 42. Otherwise, error is logged and false is returned."
  [{:keys [content]}]
  (let [n (count (string/re-split #";" content))]
    (if (= 42 n)
      true
      (do
	(logger/error (str "Invalid frame : expected 42 fields, "
			   "actual " n " fields."))
	false))))


(defn process-frame
  "If frame is valid, returns processed content. If it is not valid, the errors are logged and nil is returned"
  [frame]
  (when (every? true?
		(list
		 (valid-length? frame)
		 (valid-fields? frame)))
    (process-content (:content frame))))

(defn push
  "If frame is valid adds processed frame content to the end of the frame queue. If invalid, errors are logged and nothing is added."
  [frame]
  (when-let [pf (process-frame frame)]
    (dosync (alter *frames* conj pf))))

(defn pull
  "Removes a processed frame from the frame queue and returns the frame. If the queue is empty returns nil"
  []
  (dosync
   (when-let [frame (peek @*frames*)]
     (alter *frames* pop)
     frame)))

(defn blocking-pull
  "Like pull, only blocks until a frame can be read"
  []
  ; TODO use sleep or yeild ?
  (if-let [frame (pull)] 
    frame
    (recur)))

