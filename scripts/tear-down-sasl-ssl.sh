#!/bin/bash

rm -rf sasl-ssl/secrets

echo "Stopping docker containers..."
docker-compose -f sasl-ssl/docker-compose.yml --env-file .env down --volumes
