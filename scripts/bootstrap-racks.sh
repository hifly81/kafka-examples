#!/bin/bash

sh scripts/tear-down.sh

echo "Starting Kafka cluster..."
docker-compose -f kafka-consumer/docker-compose.yml --env-file .env up -d
