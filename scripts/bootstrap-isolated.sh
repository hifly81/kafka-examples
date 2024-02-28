#!/bin/bash

sh scripts/tear-down-isolated.sh

wget -cO - https://raw.githubusercontent.com/apache/kafka/trunk/docker/examples/jvm/cluster/isolated/plaintext/docker-compose.yml > docker-compose-isolated.yml

echo "Starting docker containers..."
docker-compose -f docker-compose-isolated.yml --env-file .env up -d
