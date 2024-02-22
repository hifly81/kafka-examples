#!/bin/bash

sh scripts/tear-down-flink.sh

echo "Starting docker containers..."
docker-compose -f flink-window-tumbling-heartbeat/docker-compose.yml --env-file .env up -d