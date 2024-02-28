#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f docker-compose-isolated.yml --env-file .env down --volumes
rm -rf docker-compose-isolated.yml