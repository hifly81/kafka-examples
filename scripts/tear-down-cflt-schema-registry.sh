#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-producer/docker-compose-cflt-sr.yml --env-file .env down --volumes
