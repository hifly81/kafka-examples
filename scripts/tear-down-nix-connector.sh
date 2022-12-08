#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-nixstats-connector/docker-compose.yml --env-file .env down --volumes
