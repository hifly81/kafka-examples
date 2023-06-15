#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f mirror-maker2/docker-compose.yml --env-file .env down --volumes
