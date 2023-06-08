#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose -f docker-compose-kraft.yml up -d
