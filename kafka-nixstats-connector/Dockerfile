FROM confluentinc/cp-kafka-connect-base:7.1.1

COPY target/kafka-nixstats-connector-0.0.1-SNAPSHOT-package.zip /tmp/kafka-nixstats-connector-0.0.1-SNAPSHOT-package.zip

RUN confluent-hub install --no-prompt /tmp/kafka-nixstats-connector-0.0.1-SNAPSHOT-package.zip