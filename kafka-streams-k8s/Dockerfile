FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/kafka-streams-k8s-1.2.1.jar /app/kafka-streams-k8s-1.2.1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/kafka-streams-k8s-1.2.1.jar"]