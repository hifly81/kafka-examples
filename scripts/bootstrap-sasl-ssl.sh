#!/bin/bash

#set -o nounset \
#    -o errexit \
#    -o verbose \
#    -o xtrace

SASL_SSL_SECRETS_FOLDER=sasl-ssl/secrets

# Cleanup files
rm -f $SASL_SSL_SECRETS_FOLDER/*.crt $SASL_SSL_SECRETS_FOLDER/*.csr $SASL_SSL_SECRETS_FOLDER/*_creds $SASL_SSL_SECRETS_FOLDER/*.jks $SASL_SSL_SECRETS_FOLDER/*.srl $SASL_SSL_SECRETS_FOLDER/*.key $SASL_SSL_SECRETS_FOLDER/*.pem $SASL_SSL_SECRETS_FOLDER/*.der $SASL_SSL_SECRETS_FOLDER/*.p12 $SASL_SSL_SECRETS_FOLDER/extfile

mkdir -p $SASL_SSL_SECRETS_FOLDER

cp sasl-ssl/config/broker_jaas.conf $SASL_SSL_SECRETS_FOLDER

openssl req -new -x509 -keyout $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.key -out $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -days 365 -subj '/CN=ca1.test.hifly.org/OU=Hifly/O=Hifly/L=Rome/ST=Lazio/C=IT' -passin pass:confluent -passout pass:confluent

keytool -genkey -noprompt \
  -alias broker \
  -dname "CN=broker,OU=Hifly,O=Hifly,L=Rome,S=Lazio,C=IT" \
  -ext "SAN=dns:broker,dns:localhost" \
  -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks \
  -keyalg RSA \
  -storepass confluent \
  -keypass confluent \
  -storetype pkcs12

keytool -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks -alias broker -certreq -file $SASL_SSL_SECRETS_FOLDER/broker.csr -storepass confluent -keypass confluent -ext "SAN=dns:broker,dns:localhost"

cat << EOF > $SASL_SSL_SECRETS_FOLDER/extfile
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no
[req_distinguished_name]
CN = broker
[v3_req]
subjectAltName = @alt_names
[alt_names]
DNS.1 = broker
DNS.2 = localhost
EOF

openssl x509 -req -CA $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -CAkey $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.key -in $SASL_SSL_SECRETS_FOLDER/broker.csr -out $SASL_SSL_SECRETS_FOLDER/broker-ca1-signed.crt -days 9999 -CAcreateserial -passin pass:confluent -extensions v3_req -extfile $SASL_SSL_SECRETS_FOLDER/extfile

keytool -noprompt -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks -alias CARoot -import -file $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -storepass confluent -keypass confluent

keytool -noprompt -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks -alias broker -import -file $SASL_SSL_SECRETS_FOLDER/broker-ca1-signed.crt -storepass confluent -keypass confluent -ext "SAN=dns:broker,dns:localhost"

keytool -noprompt -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.truststore.jks -alias CARoot -import -file $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -storepass confluent -keypass confluent

echo  "confluent" > $SASL_SSL_SECRETS_FOLDER/broker_sslkey_creds
echo  "confluent" > $SASL_SSL_SECRETS_FOLDER/broker_keystore_creds
echo  "confluent" > $SASL_SSL_SECRETS_FOLDER/broker_truststore_creds


keytool -keystore $SASL_SSL_SECRETS_FOLDER/kafka.client.truststore.jks -alias CARoot -storepass confluent -importcert -file $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -noprompt
keytool -keystore $SASL_SSL_SECRETS_FOLDER/kafka.client.keystore.jks -alias kafka-client -validity 365 -genkey -keyalg RSA -storepass confluent -keypass confluent -dname "CN=client,OU=Hifly,O=Hifly,L=Rome,ST=Lazio,C=IT" -ext "SAN=dns:client,dns:localhost"
keytool -keystore $SASL_SSL_SECRETS_FOLDER/kafka.client.keystore.jks -alias CARoot -storepass confluent -importcert -file $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -noprompt
keytool -keystore $SASL_SSL_SECRETS_FOLDER/kafka.client.keystore.jks -alias kafka-client -storepass confluent -certreq -file $SASL_SSL_SECRETS_FOLDER/kafka-client-csr -ext "SAN=dns:client,dns:localhost"
openssl x509 -req -CA $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -CAkey $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.key -in $SASL_SSL_SECRETS_FOLDER/kafka-client-csr -out $SASL_SSL_SECRETS_FOLDER/kafka-client-cer -days 365 -CAcreateserial -passin pass:confluent
keytool -keystore $SASL_SSL_SECRETS_FOLDER/kafka.client.keystore.jks -alias kafka-client -storepass confluent -importcert -file $SASL_SSL_SECRETS_FOLDER/kafka-client-cer -noprompt -ext "SAN=dns:client,dns:localhost"


echo "Starting Kafka cluster..."
docker-compose -f sasl-ssl/docker-compose.yml --env-file .env up -d