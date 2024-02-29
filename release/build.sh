#!/bin/bash

PRJ_HOME=..
SPRING_PRODUCER_HOME=../kafka-springboot-producer
SPRING_CONSUMER_HOME=../kafka-springboot-consumer


function compile {
  printf "\nCompiling..\n"
  mvn -f $1/pom.xml clean compile
}

function release {
  printf "\nPackaging..\n"
  mvn -f $1/pom.xml clean install
}

if [ "$1" == "compile" ]; then
  compile $PRJ_HOME
  compile $SPRING_PRODUCER_HOME
  compile $SPRING_CONSUMER_HOME
elif [ "$1" == "release" ]; then
  release $PRJ_HOME
  release $SPRING_PRODUCER_HOME
  release $SPRING_CONSUMER_HOME
else
  printf "\nNo option selected <compile|release>..\n"
  exit -1
fi