#!/bin/bash

echo "Starting docker containers..."
docker-compose -f kafka-connect-sink-dlq/docker-compose.yml --env-file .env up -d