#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-connect-task-distribution/docker-compose.yml --env-file .env down --volumes
