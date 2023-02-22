#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-unixcommand-connector/docker-compose.yml --env-file .env down --volumes
