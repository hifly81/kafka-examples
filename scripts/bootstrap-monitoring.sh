#!/bin/bash

sh scripts/tear-down-monitoring.sh

echo "Starting docker containers..."
docker-compose -f monitoring/docker-compose.yml --env-file .env up -d
