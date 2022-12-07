#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-oauth-kip-768/docker-compose-idp.yml --env-file .env down --volumes
docker-compose -f kafka-oauth-kip-768/docker-compose-oauth.yml --env-file .env down --volumes
