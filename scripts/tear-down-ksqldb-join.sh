#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f ksqldb-join/docker-compose.yml --env-file .env down --volumes
