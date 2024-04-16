#!/bin/bash

sh scripts/tear-down-cdc-informix.sh

echo "Building Informix Kafka Connector..."
tar xvf cdc-debezium-informix/jars/debezium-connector-informix-2.6.1.Final-plugin.tar.gz -C cdc-debezium-informix/jars/
mkdir -p cdc-debezium-informix/jars/debezium-connector-informix/lib
mv cdc-debezium-informix/jars/debezium-connector-informix/*.jar cdc-debezium-informix/jars/debezium-connector-informix/lib
cp cdc-debezium-informix/jars/ifx-changestream-client-1.1.3.jar cdc-debezium-informix/jars/debezium-connector-informix/lib
cp cdc-debezium-informix/jars/jdbc-4.50.10.jar cdc-debezium-informix/jars/debezium-connector-informix/lib
cp cdc-debezium-informix/jars/manifest.json cdc-debezium-informix/jars/debezium-connector-informix

echo "Starting kafka docker containers..."
docker-compose -f cdc-debezium-informix/docker-compose.yml --env-file .env up -d

echo "Setup Informix docker container in background..."
docker run -d --network=cdc-debezium-informix_default -v $(pwd)/cdc-debezium-informix/config/informix_ddl_sample.sql:/tmp/informix_ddl_sample.sql -it --name ifx -h ifx --privileged -e LICENSE=accept \
    -p 9088:9088 -p 9089:9089 -p 27017:27017 -p 27018:27018 -p 27883:27883 \
    ibmcom/informix-developer-database:12.10.FC11DE

echo "Wait 60 seconds..."
sleep 60

echo "Start ifx container"
docker start ifx

echo "Wait 30 seconds..."
sleep 30