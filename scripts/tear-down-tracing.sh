#!/bin/bash

echo "Stopping docker containers..."
rm -rf kafka-distributed-tracing/streams-app/target
docker compose -f kafka-distributed-tracing/docker-compose-tracing.yml --env-file .env down --volumes
