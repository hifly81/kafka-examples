#!/bin/bash

sh scripts/tear-down.sh

echo "Starting Kafka cluster..."
cd kafka-distributed-tracing/streams-app
mvn clean package
cd ../..
docker compose -f kafka-distributed-tracing/docker-compose-tracing.yml --env-file .env up -d
