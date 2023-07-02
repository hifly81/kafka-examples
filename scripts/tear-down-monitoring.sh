#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f monitoring/docker-compose.yml --env-file .env down --volumes
