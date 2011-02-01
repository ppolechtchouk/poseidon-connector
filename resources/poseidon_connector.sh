#!/bin/bash

java -server -cp lib/* poseidon.connector.core -start -p 7654

# place db.properties and log4j.properties in the current working dir
# once the server is started you can send a test message using the 
# java -server -cp lib/* poseidon.connector.core -test -p 7654