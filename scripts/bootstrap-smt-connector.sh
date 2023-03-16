#!/bin/bash

echo $PWD

cd kafka-smt-custom

echo "Create custom SMT jar..."
mvn clean install

echo "Build SMT custom Connector image..."
docker build . -t connect-custom-smt-image:1.0.0

sleep 3

cd ..

echo "Starting docker containers..."
docker-compose -f kafka-smt-custom/docker-compose.yml --env-file .env up -d

echo "Wait 50 seconds..."

sleep 50

echo "Create topic test..."
kafka-topics --bootstrap-server localhost:9092 --create --topic test --replication-factor 1 --partitions 3

sleep 5

echo "Installing mongodb sink..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-smt-custom/config/connector_mongo.json

sleep 5

echo "Produce records..."
kafka-console-producer --broker-list localhost:9092 --topic test < kafka-smt-custom/config/test.json