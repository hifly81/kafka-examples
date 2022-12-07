#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-distributed-tracing/docker-compose-tracing.yml --env-file .env down --volumes
