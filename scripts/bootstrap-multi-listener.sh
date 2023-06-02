#!/bin/bash

mkdir -p multi-listener/ssl

echo "Create CA certificates and CA key..."

openssl req -new -x509 -keyout multi-listener/ssl/ca-key -out multi-listener/ssl/ca-cert -days 365 -subj "/C=IT/ST=Lazio/L=Rome/O=Hifly/OU=Hifly/CN=hifly.org" -nodes

echo "Import CA in broker truststore..."

keytool -keystore multi-listener/ssl/kafka.broker.truststore.jks -alias CARoot -storepass changeit -importcert -file multi-listener/ssl/ca-cert -noprompt

echo "Import CA in client truststore..."

keytool -keystore multi-listener/ssl/kafka.client.truststore.jks -alias CARoot -storepass changeit -importcert -file multi-listener/ssl/ca-cert -noprompt

echo "Create broker keystore..."

keytool -keystore multi-listener/ssl/kafka.broker.keystore.jks -alias kafka-broker -validity 365 -genkey -keyalg RSA -storepass changeit -keypass changeit -dname "CN=broker,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Import CA certificate in broker keystore..."

keytool -keystore multi-listener/ssl/kafka.broker.keystore.jks -alias CARoot -storepass changeit -importcert -file multi-listener/ssl/ca-cert -noprompt

echo "Create CSR for broker certificate..."

keytool -keystore  multi-listener/ssl/kafka.broker.keystore.jks -alias kafka-broker -storepass changeit -certreq -file multi-listener/ssl/kafka-broker-csr -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Create signed certificate for broker..."

openssl x509 -req -CA multi-listener/ssl/ca-cert -CAkey multi-listener/ssl/ca-key -in multi-listener/ssl/kafka-broker-csr -out multi-listener/ssl/kafka-broker-cer -days 365 -CAcreateserial -passin pass:changeit

echo "Import broker certificate in broker keystore..."

keytool -keystore multi-listener/ssl/kafka.broker.keystore.jks -alias kafka-broker -storepass changeit -importcert -file multi-listener/ssl/kafka-broker-cer -noprompt -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Save credentials..."

echo  "changeit" > multi-listener/ssl/kafka.broker.truststore.jks_creds
echo  "changeit" > multi-listener/ssl/kafka.broker.keystore.jks_creds
echo  "changeit" > multi-listener/ssl/kafka.broker.pkey_creds


echo "Starting Kafka cluster..."
docker-compose -f multi-listener/docker-compose.yml --env-file .env up -d
