#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f multi-listener/docker-compose.yml --env-file .env down --volumes
rm -rf multi-listener/ssl
