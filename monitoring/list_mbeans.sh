#!/bin/bash

HOST=$1
PORT=$2

wget https://github.com/jiaqi/jmxterm/releases/download/v1.0.2/jmxterm-1.0.2-uber.jar

# Connect to the JMX server using jmxterm
java -jar jmxterm-1.0.2-uber.jar --url $HOST:$PORT <<EOF
beans
EOF