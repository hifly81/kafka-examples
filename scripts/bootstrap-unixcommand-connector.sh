#!/bin/bash

sh scripts/tear-down.sh

echo "Build unixcommand Connector image..."
cd kafka-unixcommand-connector
sh build-image.sh

sleep 3

cd ..

echo "Starting docker containers..."
docker-compose -f kafka-unixcommand-connector/docker-compose.yml --env-file .env up -d
