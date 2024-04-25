FROM confluentinc/cp-kafka-connect-base:7.6.1

COPY target/kafka-unixcommand-connector-1.2.1-package.zip /tmp/kafka-unixcommand-connector-1.2.1-package.zip

RUN confluent-hub install --no-prompt /tmp/kafka-unixcommand-connector-1.2.1-package.zip