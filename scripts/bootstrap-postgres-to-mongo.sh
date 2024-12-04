#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose -f postgres-to-mongo/docker-compose.yml --env-file .env up -d

sleep 30

echo "Create jdbc_accounts topic..."
docker exec -it broker kafka-topics --bootstrap-server broker:9092 --create --topic jdbc_accounts --replication-factor 1 --partitions 1

echo "Installing jdbc source..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @postgres-to-mongo/config/jdbc_psql_source.json

sleep 5

echo "Installing mongo sink..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @postgres-to-mongo/config/connector_mongo_sink.json