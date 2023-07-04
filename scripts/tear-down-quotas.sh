#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f quotas/docker-compose.yml --env-file .env down --volumes
