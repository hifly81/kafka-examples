#!/bin/bash

echo "Starting docker containers..."
docker-compose -f docker-compose-kraft.yml up -d
