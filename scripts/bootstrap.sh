#!/bin/bash

sh scripts/tear-down.sh

echo "Starting docker containers..."
docker-compose up -d
