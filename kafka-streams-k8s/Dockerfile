FROM openjdk:17.0.1-jdk-slim

# Install curl and procps (for ps command)
RUN apt-get update && apt-get install -y curl procps && rm -rf /var/lib/apt/lists/*


WORKDIR /app

COPY target/kafka-streams-k8s-1.2.1.jar /app/kafka-streams-k8s-1.2.1.jar

COPY prometheus/kafka_streams.yml /app/kafka_streams.yml
COPY prometheus/jmx_prometheus_javaagent-0.20.0.jar /app/jmx_prometheus_javaagent-0.20.0.jar

EXPOSE 8080
EXPOSE 1234

ENV JAVA_OPTS="-javaagent:/app/jmx_prometheus_javaagent-0.20.0.jar=1234:/app/kafka_streams.yml"

# ENTRYPOINT ["java", "-jar", "/app/kafka-streams-k8s-1.2.1.jar"]

CMD java $JAVA_OPTS -jar /app/kafka-streams-k8s-1.2.1.jar