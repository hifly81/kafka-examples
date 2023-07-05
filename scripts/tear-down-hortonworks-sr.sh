#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-producer/docker-compose-hortonworks-sr.yml --env-file .env down --volumes
