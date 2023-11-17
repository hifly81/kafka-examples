#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-connect-sink-http/docker-compose.yml --env-file .env down --volumes
