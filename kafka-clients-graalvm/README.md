# Pre Requisites

- Install GraalVM for your target OS and Java JDK 17 from https://www.graalvm.org/


# Create the native image

- Create package:

```
mvn clean package
```

- Generate configuration files:

```
$GRAALVM_HOME/bin/java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar target/kafka-clients-graalvm-1.2.1-jar-with-dependencies.jar
```

- Create native image

```
native-image --no-fallback \
   --initialize-at-build-time=org.slf4j.LoggerFactory,org.slf4j.impl.StaticLoggerBinder,org.slf4j.impl.SimpleLogger \
   -H:ReflectionConfigurationFiles=src/main/resources/META-INF/native-image/reflect-config.json \
   -H:ResourceConfigurationFiles=src/main/resources/META-INF/native-image/resource-config.json \
   -H:DynamicProxyConfigurationFiles=src/main/resources/META-INF/native-image/proxy-config.json \
   -H:AdditionalSecurityProviders=com.sun.security.sasl.Provider \
   -H:Name=kafka-clients-graalvm \
   -jar target/kafka-clients-graalvm-1.2.1-jar-with-dependencies.jar
```

# Testing

## No Authentication

```
chmod +x kafka-clients-graalvm-1.2.1-jar-with-dependencies
./kafka-clients-graalvm-1.2.1-jar-with-dependencies

Produce message: Hello GraalVM Kafka!
Consumed message: Hello GraalVM Kafka!
```

## SASL PLAIN Authentication with SSL

e.g. This is the typical scenario when connecting to Confluent Cloud.

```
chmod +x kafka-clients-graalvm-1.2.1-jar-with-dependencies
./kafka-clients-graalvm-1.2.1-jar-with-dependencies examples/producer.properties examples/consumer.properties

Produce message: Hello GraalVM Kafka!
Consumed message: Hello GraalVM Kafka!
```

## SASL GSSAPI Authentication

Kafka with Kerberos using docker containers:
https://github.com/Dabz/kafka-security-playbook

Start a kafka cluster with Kerberos:

```
cd kerberos
./up
```

or alternatively Start a kafka cluster with Kerberos and ad dns server (required for _dns_lookup_kdc=true_):

```
cd kerberos
./up dns
```

IMPORTANT:
_dns_lookup_kdc=true_ version is at the moment not working properly --> Cannot locate KDC

Wait for the containers to be up, then login into _client_ container

```
docker exec -it client /bin/bash
```

From _client_ container run:

```
cd kafka-examples-master/kafka-clients-graalvm/ && mvn clean package && unzip target/kafka-clients-graalvm-1.2.1-jar-with-dependencies.jar
```

Create native image:

```
/tmp/graalvm-jdk-17.0.12+8.1/bin/native-image --no-fallback \
--initialize-at-build-time=org.slf4j.LoggerFactory,org.slf4j.impl.StaticLoggerBinder,org.slf4j.impl.SimpleLogger,sun.security.jgss.krb5 \
-H:ReflectionConfigurationFiles=src/main/resources/META-INF/native-image/reflect-config.json \
-H:ResourceConfigurationFiles=src/main/resources/META-INF/native-image/resource-config.json \
-H:DynamicProxyConfigurationFiles=src/main/resources/META-INF/native-image/proxy-config.json \
-H:AdditionalSecurityProviders=sun.security.jgss.SunProvider,sun.security.provider.Sun,com.sun.security.sasl.Provider \
-H:Name=kafka-clients-graalvm \
-Djava.library.path=/tmp/kafka-examples-master/kafka-clients-graalvm/linux/aarch64 \
-jar target/kafka-clients-graalvm-1.2.1-jar-with-dependencies.jar
```

Execute native image with GSSAPI auth:

```
chmod +x kafka-clients-graalvm-1.2.1-jar-with-dependencies

./kafka-clients-graalvm-1.2.1-jar-with-dependencies -Djava.library.path=/tmp/kafka-examples-master/kafka-clients-graalvm/linux/aarch64 /etc/kafka/producer.properties /etc/kafka/consumer.properties
```
