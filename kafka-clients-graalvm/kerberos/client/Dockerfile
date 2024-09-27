FROM centos:centos8
MAINTAINER d.gasparina@gmail.com
ENV container docker

# 0. Fixing Mirror list for Centos
RUN sed -i 's/mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-Linux-*
RUN sed -i 's|#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-Linux-*

# 1. Adding Confluent repository
RUN rpm --import https://packages.confluent.io/rpm/5.4/archive.key
COPY confluent.repo /etc/yum.repos.d/confluent.repo
RUN yum clean all

# 2. Install kafka, wget, tar
RUN yum install -y java-17-openjdk
RUN yum install -y confluent-kafka-2.12
RUN yum install -y wget
RUN yum install -y tar
RUN yum install -y unzip

# 3. Install Kerberos libaries
RUN yum install -y krb5-workstation krb5-libs

# 4. Copy in required settings for client access to Kafka
COPY consumer.properties /etc/kafka/consumer.properties
COPY producer.properties /etc/kafka/producer.properties
COPY command.properties /etc/kafka/command.properties
COPY client.sasl.jaas.config /etc/kafka/client_jaas.conf

ENV KAFKA_OPTS=-Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf

WORKDIR /tmp

# Install gcc
RUN yum install -y gcc
RUN yum install -y zlib-devel

# Download GraalVM JDK 17 from Oracle and extract it to /tmp
RUN wget https://download.oracle.com/graalvm/17/latest/graalvm-jdk-17_linux-aarch64_bin.tar.gz
RUN tar -xvzf graalvm-jdk-17_linux-aarch64_bin.tar.gz
RUN rm graalvm-jdk-17_linux-aarch64_bin.tar.gz

# Download Maven and extract it to /tmp
RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
RUN tar -xvzf apache-maven-3.9.9-bin.tar.gz
RUN rm apache-maven-3.9.9-bin.tar.gz

RUN wget https://github.com/hifly81/kafka-clients-graalvm-playground/archive/refs/heads/master.zip
RUN unzip master.zip

ENV MAVEN_HOME=/tmp/apache-maven-3.9.9
ENV PATH=$MAVEN_HOME/bin:$PATH

CMD sleep infinity
