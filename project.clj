(defproject poseidon-connector "1.1.0"
  :description "A connector between POSEIDON TCP stream and e-Portal DB"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [log4j "1.2.15" :exclusions
		  [javax.mail/mail
		   javax.jms/jms
		   com.sun.jdmk/jmxtools
		   com.sun.jmx/jmxri] ]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :repl-port 4001
  :repl-host "0.0.0.0"
  )
