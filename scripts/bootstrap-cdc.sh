#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose -f cdc-debezium-postgres/docker-compose.yml --env-file .env up -d