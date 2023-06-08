#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose -f kafka-connect-task-distribution/docker-compose.yml --env-file .env up -d

echo "Wait 50 seconds..."

sleep 50

echo "Installing datagen pageviews..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-connect-task-distribution/config/connector_datagen.json

echo "Wait 15 seconds..."

sleep 15

echo "datagen pageviews status..."
curl -v http://localhost:8083/connectors?expand=status

sleep 15

echo "Stop connect2..."
docker stop connect2

echo "datagen pageviews status..."
curl -v http://localhost:8083/connectors?expand=status