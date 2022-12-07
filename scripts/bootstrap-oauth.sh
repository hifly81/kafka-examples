#!/bin/bash

echo "Starting IDP... (wait 15 seconds)"
docker-compose -f kafka-oauth-kip-768/docker-compose-idp.yml --env-file .env up -d

sleep 15

echo "Starting Kafka cluster..."
docker-compose -f kafka-oauth-kip-768/docker-compose-oauth.yml --env-file .env up -d
