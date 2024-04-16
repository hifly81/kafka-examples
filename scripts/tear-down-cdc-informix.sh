#!/bin/bash

echo "Stopping kafka docker containers..."
docker-compose -f cdc-debezium-informix/docker-compose.yml --env-file .env down --volumes

echo "Stopping ifx.."
docker rm -f ifx

rm -rf cdc-debezium-informix/jars/debezium-connector-informix
rm -rf cdc-debezium-informix/jars/debezium-connector-informix-2.6.1.Final-plugin.tar.gz
