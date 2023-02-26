#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f authorizers/docker-compose.yml --env-file .env down --volumes
