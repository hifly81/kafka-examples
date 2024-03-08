#!/bin/bash

sh scripts/tear-down-connect-source-sap-hana.sh

wget -P kafka-connect-source-sap-hana/config https://github.com/SAP/kafka-connect-sap/releases/download/0.9.4/kafka-connector-hana_2.13-0.9.4.jar
wget -P kafka-connect-source-sap-hana/config https://repo1.maven.org/maven2/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar
wget -P kafka-connect-source-sap-hana/config https://repo1.maven.org/maven2/com/sap/cloud/db/jdbc/ngdbc/2.10.14/ngdbc-2.10.14.jar

mkdir -p kafka-connect-source-sap-hana/data/hana
chmod a+rwx kafka-connect-source-sap-hana/data/hana

echo "Starting Kafka cluster..."
docker-compose -f kafka-connect-source-sap-hana/docker-compose.yml --env-file .env up -d

echo "Wait 120 seconds..."

sleep 120

echo "Create testtopic..."
docker exec -it broker kafka-topics --bootstrap-server broker:9092 --create --topic testtopic --replication-factor 1 --partitions 1

echo "Installing sap hana source..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-connect-source-sap-hana/config/sap_hana_source.json

echo "Wait 3 seconds..."

sleep 3

echo "connectors status..."
curl -v http://localhost:8083/connectors?expand=status