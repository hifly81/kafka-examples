#!/bin/bash

sh scripts/tear-down.sh

echo "Starting Kafka cluster..."
docker-compose -f kafka-distributed-tracing/docker-compose-tracing.yml --env-file .env up -d
