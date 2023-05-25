#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-producer/docker-compose-apicurio.yml --env-file .env down --volumes
