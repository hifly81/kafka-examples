#!/bin/bash

echo "Build nixstats Connector image..."
cd kafka-nixstats-connector/
./build-image.sh

sleep 3

cd ..

echo "Starting docker containers..."
docker-compose up -d
