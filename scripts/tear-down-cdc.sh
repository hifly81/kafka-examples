#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f cdc-debezium-postgres/docker-compose.yml --env-file .env down --volumes
