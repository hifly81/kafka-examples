#!/bin/bash

mkdir -p mtls-listener/ssl

echo "Create CA certificates and CA key..."

openssl req -new -x509 -keyout mtls-listener/ssl/ca-key -out mtls-listener/ssl/ca-cert -days 365 -subj "/C=IT/ST=Lazio/L=Rome/O=Hifly/OU=Hifly/CN=hifly.org" -nodes

echo "Import CA in broker truststore..."

keytool -keystore mtls-listener/ssl/kafka.broker.truststore.jks -alias CARoot -storepass changeit -importcert -file mtls-listener/ssl/ca-cert -noprompt

echo "Import CA in client truststore..."

keytool -keystore mtls-listener/ssl/kafka.client.truststore.jks -alias CARoot -storepass changeit -importcert -file mtls-listener/ssl/ca-cert -noprompt

echo "Create broker keystore..."

keytool -keystore mtls-listener/ssl/kafka.broker.keystore.jks -alias kafka-broker -validity 365 -genkey -keyalg RSA -storepass changeit -keypass changeit -dname "CN=broker,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Create client keystore..."

keytool -keystore mtls-listener/ssl/kafka.client.keystore.jks -alias kafka-client -validity 365 -genkey -keyalg RSA -storepass changeit -keypass changeit -dname "CN=client,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:*.hifly.org,dns:client,dns:localhost"

echo "Import CA certificate in broker keystore..."

keytool -keystore mtls-listener/ssl/kafka.broker.keystore.jks -alias CARoot -storepass changeit -importcert -file mtls-listener/ssl/ca-cert -noprompt

echo "Import CA certificate in client keystore..."

keytool -keystore mtls-listener/ssl/kafka.client.keystore.jks -alias CARoot -storepass changeit -importcert -file mtls-listener/ssl/ca-cert -noprompt

echo "Create CSR for broker certificate..."

keytool -keystore  mtls-listener/ssl/kafka.broker.keystore.jks -alias kafka-broker -storepass changeit -certreq -file mtls-listener/ssl/kafka-broker-csr -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Create CSR for client certificate..."

keytool -keystore  mtls-listener/ssl/kafka.client.keystore.jks -alias kafka-client -storepass changeit -certreq -file mtls-listener/ssl/kafka-client-csr -ext "SAN=dns:*.hifly.org,dns:client,dns:localhost"

echo "Create signed certificate for broker..."

openssl x509 -req -CA mtls-listener/ssl/ca-cert -CAkey mtls-listener/ssl/ca-key -in mtls-listener/ssl/kafka-broker-csr -out mtls-listener/ssl/kafka-broker-cer -days 365 -CAcreateserial -passin pass:changeit

echo "Create signed certificate for client..."

openssl x509 -req -CA mtls-listener/ssl/ca-cert -CAkey mtls-listener/ssl/ca-key -in mtls-listener/ssl/kafka-client-csr -out mtls-listener/ssl/kafka-client-cer -days 365 -CAcreateserial -passin pass:changeit

echo "Import broker certificate in broker keystore..."

keytool -keystore mtls-listener/ssl/kafka.broker.keystore.jks -alias kafka-broker -storepass changeit -importcert -file mtls-listener/ssl/kafka-broker-cer -noprompt -ext "SAN=dns:*.hifly.org,dns:broker,dns:localhost"

echo "Import client certificate in client keystore..."

keytool -keystore mtls-listener/ssl/kafka.client.keystore.jks -alias kafka-client -storepass changeit -importcert -file mtls-listener/ssl/kafka-client-cer -noprompt -ext "SAN=dns:*.hifly.org,dns:client,dns:localhost"

echo "Save credentials..."

echo  "changeit" > mtls-listener/ssl/kafka.broker.truststore.jks_creds
echo  "changeit" > mtls-listener/ssl/kafka.broker.keystore.jks_creds
echo  "changeit" > mtls-listener/ssl/kafka.broker.pkey_creds


echo "Starting Kafka cluster..."
docker-compose -f mtls-listener/docker-compose-mtls.yml --env-file .env up -d
