#!/bin/bash

cd performance
ls -ltr

echo "Build kafka perf image..."
sh build-image.sh

sleep 3

cd ..

echo "Starting docker containers..."
docker-compose -f performance/docker-compose.yml --env-file .env up -d
