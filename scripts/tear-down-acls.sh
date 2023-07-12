#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f acls/docker-compose.yml --env-file .env down --volumes
