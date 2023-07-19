#!/bin/bash

sh scripts/tear-down.sh

echo "Starting Kafka cluster..."
docker-compose -f claim-check/docker-compose.yml --env-file .env up -d
