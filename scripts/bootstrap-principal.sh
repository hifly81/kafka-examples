#!/bin/bash

sh scripts/tear-down.sh

cd principal-builder
mvn clean package

cd ..

mkdir -p principal-builder/ssl

echo "Create CA certificates and CA key..."

openssl req -new -x509 -keyout principal-builder/ssl/ca-key -out principal-builder/ssl/ca-cert -days 365 -subj "/C=IT/ST=Lazio/L=Rome/O=Hifly/OU=Hifly/CN=hifly.org" -nodes

echo "Import CA in broker truststore..."

keytool -keystore principal-builder/ssl/kafka.broker.truststore.jks -alias CARoot -storepass changeit -importcert -file principal-builder/ssl/ca-cert -noprompt

echo "Import CA in client truststore..."

keytool -keystore principal-builder/ssl/kafka.client.truststore.jks -alias CARoot -storepass changeit -importcert -file principal-builder/ssl/ca-cert -noprompt

echo "Import CA in client 2 truststore..."

keytool -keystore principal-builder/ssl/kafka.client2.truststore.jks -alias CARoot -storepass changeit -importcert -file principal-builder/ssl/ca-cert -noprompt

echo "Create broker keystore..."

keytool -keystore principal-builder/ssl/kafka.broker.keystore.jks -alias kafka-broker -validity 365 -genkey -keyalg RSA -storepass changeit -keypass changeit -dname "CN=broker,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Create client keystore..."

keytool -keystore principal-builder/ssl/kafka.client.keystore.jks -alias kafka-client -validity 365 -genkey -keyalg RSA -storepass changeit -keypass changeit -dname "CN=client,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:*.hifly.org,dns:client,dns:localhost"

echo "Create client2 keystore..."

keytool -keystore principal-builder/ssl/kafka.client2.keystore.jks -alias kafka-client -validity 365 -genkey -keyalg RSA -storepass changeit -keypass changeit -dname "CN=client2,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:*.hifly.org,dns:client2,dns:localhost"

echo "Import CA certificate in broker keystore..."

keytool -keystore principal-builder/ssl/kafka.broker.keystore.jks -alias CARoot -storepass changeit -importcert -file principal-builder/ssl/ca-cert -noprompt

echo "Import CA certificate in client keystore..."

keytool -keystore principal-builder/ssl/kafka.client.keystore.jks -alias CARoot -storepass changeit -importcert -file principal-builder/ssl/ca-cert -noprompt

echo "Import CA certificate in client2 keystore..."

keytool -keystore principal-builder/ssl/kafka.client2.keystore.jks -alias CARoot -storepass changeit -importcert -file principal-builder/ssl/ca-cert -noprompt

echo "Create CSR for broker certificate..."

keytool -keystore  principal-builder/ssl/kafka.broker.keystore.jks -alias kafka-broker -storepass changeit -certreq -file principal-builder/ssl/kafka-broker-csr -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Create CSR for client certificate..."

keytool -keystore  principal-builder/ssl/kafka.client.keystore.jks -alias kafka-client -storepass changeit -certreq -file principal-builder/ssl/kafka-client-csr -ext "SAN=dns:*.hifly.org,dns:client,dns:localhost"

echo "Create CSR for client2 certificate..."

keytool -keystore  principal-builder/ssl/kafka.client2.keystore.jks -alias kafka-client -storepass changeit -certreq -file principal-builder/ssl/kafka-client2-csr -ext "SAN=dns:*.hifly.org,dns:client2,dns:localhost"

echo "Create signed certificate for broker..."

openssl x509 -req -CA principal-builder/ssl/ca-cert -CAkey principal-builder/ssl/ca-key -in principal-builder/ssl/kafka-broker-csr -out principal-builder/ssl/kafka-broker-cer -days 365 -CAcreateserial -passin pass:changeit

echo "Create signed certificate for client..."

openssl x509 -req -CA principal-builder/ssl/ca-cert -CAkey principal-builder/ssl/ca-key -in principal-builder/ssl/kafka-client-csr -out principal-builder/ssl/kafka-client-cer -days 365 -CAcreateserial -passin pass:changeit

echo "Create signed certificate for client2..."

openssl x509 -req -CA principal-builder/ssl/ca-cert -CAkey principal-builder/ssl/ca-key -in principal-builder/ssl/kafka-client2-csr -out principal-builder/ssl/kafka-client2-cer -days 365 -CAcreateserial -passin pass:changeit

echo "Import broker certificate in broker keystore..."

keytool -keystore principal-builder/ssl/kafka.broker.keystore.jks -alias kafka-broker -storepass changeit -importcert -file principal-builder/ssl/kafka-broker-cer -noprompt -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Import client certificate in client keystore..."

keytool -keystore principal-builder/ssl/kafka.client.keystore.jks -alias kafka-client -storepass changeit -importcert -file principal-builder/ssl/kafka-client-cer -noprompt -ext "SAN=dns:*.hifly.org,dns:client,dns:localhost"

echo "Import client2 certificate in client keystore..."

keytool -keystore principal-builder/ssl/kafka.client2.keystore.jks -alias kafka-client -storepass changeit -importcert -file principal-builder/ssl/kafka-client2-cer -noprompt -ext "SAN=dns:*.hifly.org,dns:client2,dns:localhost"

echo "Save credentials..."

echo  "changeit" > principal-builder/ssl/kafka.broker.truststore.jks_creds
echo  "changeit" > principal-builder/ssl/kafka.broker.keystore.jks_creds
echo  "changeit" > principal-builder/ssl/kafka.broker.pkey_creds


echo "Starting Kafka cluster..."
docker-compose -f principal-builder/docker-compose.yml --env-file .env up -d
