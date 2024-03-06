#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-connect-sink-s3/docker-compose.yml --env-file .env down --volumes