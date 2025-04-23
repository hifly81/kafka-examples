#!/bin/bash

sh scripts/tear-down.sh

echo "Starting IDP... (wait 15 seconds)"
docker-compose -f kafka-oauth-kip-768/docker-compose-idp.yml --env-file .env up -d
