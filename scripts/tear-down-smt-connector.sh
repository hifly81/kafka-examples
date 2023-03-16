#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-smt-custom/docker-compose.yml --env-file .env down --volumes
