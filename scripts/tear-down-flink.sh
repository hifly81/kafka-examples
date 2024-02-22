#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f flink-window-tumbling-heartbeat/docker-compose.yml --env-file .env down --volumes
