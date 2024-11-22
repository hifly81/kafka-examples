#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose -f kafka-connect-source-event-router/docker-compose.yml --env-file .env up -d

echo "Wait 20 seconds..."

sleep 20

echo "Installing jdbc source..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-connect-source-event-router/config/connector_jdbc_source.json

echo "Wait 3 seconds..."

sleep 3

echo "connectors status..."
curl -v http://localhost:8083/connectors?expand=status