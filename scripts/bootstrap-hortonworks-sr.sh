#!/bin/bash

sh scripts/tear-down.sh

echo "Starting Kafka cluster..."
docker-compose -f kafka-producer/docker-compose-hortonworks-sr.yml --env-file .env up -d
