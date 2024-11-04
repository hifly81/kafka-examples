#!/bin/bash

echo "Stopping kafka docker containers..."
docker-compose -f cdc-debezium-mongo/docker-compose.yml --env-file .env down --volumes
