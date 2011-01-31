(ns poseidon-connector.test.network
  (:use [poseidon-connector.network] :reload-all)
  (:require [poseidon-connector.frame :as frame :only [blocking-pull process-content]])
  (:use [clojure.test]))

(def frame1 (str (char 1) "!65129405;00000000;000020;2043129405;091218;094557;90;2205;0;0;0;091218;094644000;0;0;999;0;0;04;0;0;0;0;0;0;0;;1;0;0;0088;1;0;;;;;07;$UTS1;10.100.103.23;DIAG;                                                                                                                                 "))
(deftest frame-test
  (start-server 8765)
  (test-message 8765)
  (is (=  (frame/blocking-pull)
	  (frame/process-content(apply str (drop 2 frame1)))))
  (stop-server 8765)
  )


