#!/bin/bash

echo "Starting docker containers..."
docker-compose -f ksqldb-join/docker-compose.yml --env-file .env up -d