#!/bin/bash

echo "Build nixstats Connector image..."
kafka-nixstats-connector/build-image.sh

sleep 3

cd ..

echo "Starting docker containers..."
docker-compose -f kafka-nixstats-connector/docker-compose.yml --env-file .env up -d
