#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f principal-builder/docker-compose.yml --env-file .env down --volumes
rm -rf principal-builder/ssl
