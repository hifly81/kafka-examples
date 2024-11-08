FROM openjdk:17-jdk-slim-buster

RUN apt-get update -y
RUN apt-get install -y wget
RUN apt-get install -y bc

RUN wget https://downloads.apache.org/kafka/3.9.0/kafka_2.13-3.9.0.tgz \
&& tar zxvf kafka_2.13-3.9.0.tgz && rm kafka_2.13-3.9.0.tgz

WORKDIR /kafka_2.13-3.9.0/bin