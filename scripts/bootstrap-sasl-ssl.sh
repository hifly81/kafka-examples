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

# Generate CA key
openssl req -new -x509 -keyout $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.key -out $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -days 365 -subj '/CN=ca1.test.confluent.io/OU=TEST/O=CONFLUENT/L=PaloAlto/ST=Ca/C=US' -passin pass:confluent -passout pass:confluent

# Create host keystore
keytool -genkey -noprompt \
  -alias broker \
  -dname "CN=broker,OU=TEST,O=CONFLUENT,L=PaloAlto,S=Ca,C=US" \
  -ext "SAN=dns:broker,dns:localhost" \
  -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks \
  -keyalg RSA \
  -storepass confluent \
  -keypass confluent \
  -storetype pkcs12

# Create the certificate signing request (CSR)
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

# Sign the host certificate with the certificate authority (CA)
openssl x509 -req -CA $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -CAkey $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.key -in $SASL_SSL_SECRETS_FOLDER/broker.csr -out $SASL_SSL_SECRETS_FOLDER/broker-ca1-signed.crt -days 9999 -CAcreateserial -passin pass:confluent -extensions v3_req -extfile $SASL_SSL_SECRETS_FOLDER/extfile

# Sign and import the CA cert into the keystore
keytool -noprompt -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks -alias CARoot -import -file $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -storepass confluent -keypass confluent

# Sign and import the host certificate into the keystore
keytool -noprompt -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.keystore.jks -alias broker -import -file $SASL_SSL_SECRETS_FOLDER/broker-ca1-signed.crt -storepass confluent -keypass confluent -ext "SAN=dns:broker,dns:localhost"

# Create truststore and import the CA cert
keytool -noprompt -keystore $SASL_SSL_SECRETS_FOLDER/kafka.broker.truststore.jks -alias CARoot -import -file $SASL_SSL_SECRETS_FOLDER/snakeoil-ca-1.crt -storepass confluent -keypass confluent

# Save creds
echo  "confluent" > $SASL_SSL_SECRETS_FOLDER/broker_sslkey_creds
echo  "confluent" > $SASL_SSL_SECRETS_FOLDER/broker_keystore_creds
echo  "confluent" > $SASL_SSL_SECRETS_FOLDER/broker_truststore_creds

echo "Starting Kafka cluster..."
docker-compose -f sasl-ssl/docker-compose.yml --env-file .env up -d