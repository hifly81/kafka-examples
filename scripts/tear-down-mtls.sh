#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f mtls-listener/docker-compose-mtls.yml --env-file .env down --volumes
rm -rf mtls-listener/ssl
