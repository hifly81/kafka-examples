#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f ksqldb/docker-compose.yml --env-file .env down --volumes
