#!/bin/bash

echo "Starting Kafka cluster..."
docker-compose -f authorizers/docker-compose.yml --env-file .env up -d
