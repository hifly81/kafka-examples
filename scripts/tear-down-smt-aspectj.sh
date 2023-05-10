#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-smt-aspectj/docker-compose.yml --env-file .env down --volumes
