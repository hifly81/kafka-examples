#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f claim-check/docker-compose.yml --env-file .env down --volumes
