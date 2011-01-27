(ns poseidon-connector.test.network
  (:use [poseidon-connector.network] :reload-all)
  (:use [clojure.test]))

(def frame1 (str (char 1) "!65129405;00000000;000020;2043129405;091218;094557;90;2205;0;0;0;091218;094644000;0;0;999;0;0;04;0;0;0;0;0;0;0;;1;0;0;0088;1;0;;;;;07;$UTS1;10.100.103.23;DIAG;                                                                                                                                 "))



(swap! message (fn [_] nil)) ;reset message

(deftest frame-test
  (let [server (start-server 9765)
	msg (do (send-message (start-client 9765) frame1)
		      @message)]
    (is (= (apply str (drop 2 frame1)) msg)
	"Frame processed incorrectly")
    (stop server)
    ))


