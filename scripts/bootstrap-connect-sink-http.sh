#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose -f kafka-connect-sink-http/docker-compose.yml --env-file .env up -d

echo "Wait 20 seconds..."

sleep 20

echo "Create topicA..."
docker exec -it broker kafka-topics --bootstrap-server broker:9092 --create --topic topicA --replication-factor 1 --partitions 1

echo "Installing http sink..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-connect-sink-http/config/http_sink.json

echo "Wait 3 seconds..."

sleep 3

echo "connectors status..."
curl -v http://localhost:8083/connectors?expand=status

sleep 5

echo "start http demo..."
cd kafka-connect-sink-http/rest-controller
mvn spring-boot:run