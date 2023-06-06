#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f docker-compose-kraft.yml down --volumes
