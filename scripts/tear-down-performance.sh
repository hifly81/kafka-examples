#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f performance/docker-compose.yml --env-file .env down --volumes
