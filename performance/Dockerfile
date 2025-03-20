FROM openjdk:17-jdk-slim-buster

RUN apt-get update -y
RUN apt-get install -y wget
RUN apt-get install -y bc

RUN wget https://dlcdn.apache.org/kafka/4.0.0/kafka_2.13-4.0.0.tgz \
&& tar zxvf kafka_2.13-4.0.0.tgz && rm kafka_2.13-4.0.0.tgz

WORKDIR /kafka_2.13-4.0.0/bin