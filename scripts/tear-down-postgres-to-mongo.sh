#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f postgres-to-mongo/docker-compose.yml --env-file .env down --volumes
