#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-consumer/docker-compose.yml --env-file .env down --volumes
