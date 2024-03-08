#!/bin/bash

echo "Stopping docker containers..."
docker-compose -f kafka-connect-source-sap-hana/docker-compose.yml --env-file .env down --volumes

rm -rf kafka-connect-source-sap-hana/data
rm -rf kafka-connect-source-sap-hana/config/*.jar