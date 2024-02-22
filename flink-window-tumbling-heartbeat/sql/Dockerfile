###############################################################################
# inspired by
# - https://github.com/wuchong/flink-sql-demo/tree/v1.11-EN/sql-client
# - https://github.com/theodorecurtil/flink_sql_job
###############################################################################

FROM flink:1.18.1-java17

COPY sql/bin/* /opt/sql-client/
RUN mkdir -p /opt/sql-client/lib

RUN wget -P /opt/sql-client/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka/3.1.0-1.18/flink-sql-connector-kafka-3.1.0-1.18.jar; \
    wget -P /opt/sql-client/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-json/1.18.1/flink-json-1.18.1.jar; \
    wget -P /opt/sql-client/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-avro-confluent-registry/1.18.1/flink-sql-avro-confluent-registry-1.18.1.jar;

COPY sql/conf/* /opt/flink/conf/
COPY sql/app/* /opt/sql-client/app/

WORKDIR /opt/sql-client
ENV SQL_CLIENT_HOME /opt/sql-client

COPY sql/docker-entrypoint.sh /
RUN ["chmod", "+x", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]