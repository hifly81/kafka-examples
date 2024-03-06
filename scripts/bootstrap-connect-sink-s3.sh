#!/bin/bash

sh scripts/tear-down-connect-sink-s3.sh

echo "Starting Kafka cluster..."
docker-compose -f kafka-connect-sink-s3/docker-compose.yml --env-file .env up -d

echo "Wait 20 seconds..."

sleep 20

echo "Create gaming-player-activity..."
docker exec -it broker kafka-topics --bootstrap-server broker:9092 --create --topic gaming-player-activity --replication-factor 1 --partitions 1

echo "Installing s3 sink..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-connect-sink-s3/config/s3_sink.json

echo "Wait 3 seconds..."

sleep 3

echo "connectors status..."
curl -v http://localhost:8083/connectors?expand=status