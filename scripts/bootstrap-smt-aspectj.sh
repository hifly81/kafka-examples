#!/bin/bash

sh scripts/tear-down.sh

echo $PWD

cd kafka-smt-aspectj

echo "Create custom SMT jar..."
mvn clean install

echo "Build SMT custom Connector image..."
docker build . -t connect-custom-smt-image:1.0.0

sleep 3

cd ..

echo "Starting docker containers..."
docker-compose -f kafka-smt-aspectj/docker-compose.yml --env-file .env up -d

echo "Wait 30 seconds..."

sleep 30

echo "Create topic test..."
kafka-topics --bootstrap-server localhost:9092 --create --topic test --replication-factor 1 --partitions 3

sleep 5

echo "Installing mongodb sink..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-smt-aspectj/config/connector_mongo.json

sleep 5

echo "Produce records..."
kafka-console-producer --broker-list localhost:9092 --topic test < kafka-smt-aspectj/config/test.json