FROM confluentinc/cp-kafka-connect-base:7.3.0

COPY target/kafka-unixcommand-connector-0.0.1-SNAPSHOT-package.zip /tmp/kafka-unixcommand-connector-0.0.1-SNAPSHOT-package.zip

RUN confluent-hub install --no-prompt /tmp/kafka-unixcommand-connector-0.0.1-SNAPSHOT-package.zip