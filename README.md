# Examples with Apache Kafka

## Apache Kafka installation

A kafka cluster with a minimum of 1 broker (suggested 3) is need to test the examples; for details about the installation, info at:  
<https://kafka.apache.org/documentation/#quickstart>

If you want to run the nixtstats connector example, use the Docker installation.

## Installation using Docker

If you want to run your kafka cluster with docker, use the *docker-compose.yml* file in the root directory.
Images are downloaded from *confluentinc* and are based on *Confluent 6.1.x* version:
- Zookeeper: confluentinc/cp-zookeeper:6.1.0
- Kafka: confluentinc/cp-kafka:6.1.0
- Schema Registry: confluentinc/cp-schema-registry:6.1.0
- Connect: custom image based on confluentinc/cp-kafka-connect-base:6.1.0
- ksqlDB server: confluentinc/ksqldb-server:0.15.0
- ksqlDB cli: confluentinc/ksqldb-cli:0.15.0

Components list:
- Broker will listen to *localhost:29092*
- Schema Registry will listen to *localhost:8081*
- Connect will listen to *localhost:8083*
- ksqlDB cli listen to *localhost:8888*

### Create connect custom image with *nixstats connector*:

```bash
./kafka-nixstats-connector/build-image.sh
```

### Start containers:

```bash
docker-compose up -d
```

### Stop containers:

```bash
docker-compose stop
```

## Apache Kafka installation on Kubernetes and OpenShift

If you want to run your kafka cluster on Kubernetes or OpenShift, have a look at Strimzi project:  
<https://strimzi.io>

## Kafka producers

Some implementations of kafka producers.

kafka producers available:

- base: uses a *org.apache.kafka.common.serialization.StringSerializer* for key and value
- json: uses a *org.apache.kafka.common.serialization.StringSerializer* for key and a *org.hifly.kafka.demo.producer.serializer.json.JsonSerializer* for value
- avro-confluent: uses a *io.confluent.kafka.serializers.KafkaAvroSerializer* for key and value.  
A running confluent schema registry is need to register the avro schema.  
Info at: <https://github.com/confluentinc/schema-registry>
- avro-apicurio: uses a *io.apicurio.registry.utils.serde.AvroKafkaSerializer* for key and value.  
A running apicurio schema registry is need to register the avro schema.  
Info at: <https://github.com/Apicurio/apicurio-registry>
- partitioner: use a custom partitioner for keys.
A topic with 3 partitions named "demo-2" must exists.

Execute tests:

```bash
cd kafka-producer
mvn clean test
```

Every producer implementation has its own *Runner* java class producing a bunch of sample messages.
At least a kafka broker listening on port 9092 is required.

```bash
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="org.hifly.kafka.demo.producer.serializer.base.Runner"
```

```bash
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="org.hifly.kafka.demo.producer.serializer.json.Runner"
```

```bash
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="org.hifly.kafka.demo.producer.serializer.avro.RunnerConfluent"
```

```bash
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="org.hifly.kafka.demo.producer.serializer.avro.RunnerApicurio"
```

```bash
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="org.hifly.kafka.demo.producer.serializer.partitioner.custom.Runner"
```

## Kafka consumers

Implementation of a kafka consumer that can be used with variuos deserializer classes.

Execute tests:

```bash
cd kafka-consumer
mvn clean test
```

Every consumer implementation has its own *Runner* java class consuming a bunch of sample messages.
At least a kafka broker listening on port 9092 is required.

```bash
cd kafka-consumer
mvn clean compile && mvn exec:java -Dexec.mainClass="org.hifly.kafka.demo.consumer.base.Runner"
```

## Kafka streams

Implementation of a series of kafka streams topoligies to analyze car data.  

CarSensorStream:  
The stream filters out speed data from car data sensor records. Speed limit is set to 150km/h and only events exceeding the limits are filtered out.  
A ktable stores the car info data.  
A left join between the kstream and the ktable produces a new aggregated object published to an output topic.

CarBrandStream:  
The stream splits the original data into 2 different topics, one for Ferrari cars and one for all other car brands.

Execute tests:

```bash
cd kafka-streams
mvn clean test
```

## Kafka Orders Transactional

Example of a cart application implementing end-to-end exactly-once semantic between consumer and producer.  
The ItemsProducer class sends 2 items in a single transaction.  
The ItemsConsumer class receives the items and creates an order containing the items.  
The consumer offset is committed only if the order can be created and sent.

Execute tests:

```bash
cd kafka-orders-tx
mvn clean test
```

At least a kafka broker listening on port 9092 is required.

Execute the ItemsProducer class:  

```bash
cd kafka-orders-tx
mvn clean compile && mvn exec:java -Dexec.mainClass="ItemsProducer"
```

Execute the ItemsConsumer class:  

```bash
cd kafka-orders-tx
mvn clean compile && mvn exec:java -Dexec.mainClass="ItemsConsumer"
```

## Kafka Spring Boot

Sample of a kafka producer and consumer implemented with Spring Boot 2.x.
They can also run on Docker and OpenShift.

At least a kafka broker listening on port 9092 is required.

Kafka Consumer implements a DLQ for records not processable (after 3 attemps).

Run on your local machine:  

```bash
#start a producer on port 8010
cd kafka-springboot-producer
mvn spring-boot:run

#start a consumer on port 8090
cd kafka-springboot-consumer
mvn spring-boot:run

#Send orders (on topic demoTopic)
curl --data '{"id":5, "name": "PS5"}' -H "Content-Type:application/json" http://localhost:8010/api/order

#Send ERROR orders and test DLQ (on topic demoTopic)
curl --data '{"id":5, "name": "ERROR-PS5"}' -H "Content-Type:application/json" http://localhost:8010/api/order
```

## Kafka Quarkus

Sample of a kafka producer and consumer implemented with Quarkus.
Every 1s a new message is sent to demo topic.

At least a kafka broker listening on port 9092 is required.

Run on your local machine:  

```bash
cd kafka-quarkus
./mvnw clean compile quarkus:dev (debug port 5005)
```

Run on Openshift machine:  

```bash
cd kafka-quarkus
./mvnw clean package -Dquarkus.container-image.build=true -Dquarkus.kubernetes.deploy=true
```

## Kafka microprofile2

Sample of a kafka producer and consumer running on a open liberty MicroProfile v2 runtime.
They can also run on Docker and OpenShift.

Run on docker:  

```bash
#Start a zookeeper container
docker run -d --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 debezium/zookeeper

#Start a kafka container
docker run -d --name my-cluster-kafka-bootstrap -p 9092:9092 --link zookeeper:zookeeper debezium/kafka

#Start a kafka producer container
cd kafka-microprofile2-producer
docker build -t kafka-producer:latest .
docker run -d --name kafka-producer -p 9080:9080 -e KAFKABROKERLIST=my-cluster-kafka-bootstrap:9092 --link my-cluster-kafka-bootstrap:my-cluster-kafka-bootstrap kafka-producer:latest

#Start a kafka consumer container
cd kafka-microprofile2-consumer
docker build -t kafka-consumer:latest .
docker run -d --name kafka-consumer -p 9090:9080 -e KAFKABROKERLIST=my-cluster-kafka-bootstrap:9092 --link my-cluster-kafka-bootstrap:my-cluster-kafka-bootstrap kafka-consumer:latest

#Receive orders
curl -v -X POST http://localhost:9090/kafka-microprofile2-consumer-0.0.1-SNAPSHOT/order

#Send orders (500)
curl -v -X POST http://localhost:9080/kafka-microprofile2-producer-0.0.1-SNAPSHOT/order
```

## Kafka nixstats Connector

Implementation of a sample Source Connector; it executes *nix commands* (e.g. *ls -ltr, netstat*) and sends its output to a kafka topic.
This connector relies on Confluent Schema Registry to convert the values using Avro: *CONNECT_VALUE_CONVERTER: io.confluent.connect.avro.AvroConverter*.

Connector config is in *kafka-nixstats-connector/config/source.quickstart.json* file.

Parameters for source connector:
- command --> nix command to execute (e.g. ls -ltr)
- topic --> output topic
- poll.ms --> poll interval in milliseconds between every executions 

### Create the connector package:

```bash
cd kafka-nixtstats-connector
mvn clean package
```

### Create a connect custom Docker image with the connector installed:
This will create an image based on *confluentinc/cp-kafka-connect-base:6.0.2* using a custom *Dockerfile*.
It will use the Confluent utility *confluent-hub install* to install the plugin in connect.

```bash
cd kafka-nixstats-connector
./kafka-nixstats-connector/build-image.sh
```

### Run the Docker container:

```bash
docker-compose up -d
```

### Install te connector

```bash
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @kafka-nixstats-connector/config/source.quickstart.json
```

## ksqlDB Sample App

Implementation of a sample App (kafka producer and consumer) sending and receiving orders; ksqlDB acts as an orchestrator to coordinate a sample Saga.

### Compile

```bash
cd ksqldb-sample
mvn schema-registry:download
mvn generate-sources
mvn clean compile
```

### Launch on local environment

Launch Docker Compose:

```bash
docker-compose up
```

Connect to ksqlDB and set auto.offset.reset:

```bash
ksql http://ksqldb-server:8088
SET 'auto.offset.reset' = 'earliest';
```

Create DDL on ksqlDB:

```bash
/ksqldb-sample/ksql/./ksql-statements.sh
```

Insert entries on ksqlDB:

```bash
/ksqldb-sample/ksql/./ksql-insert.sh
```

Create fat jar of Sample application (1 Saga):

```bash
cd ksqldb-sample
mvn clean compile assembly:single
```

Execute fat jar of Sample application (1 Saga):

```bash
cd ksqldb-sample
java -jar payment-0.0.1-jar-with-dependencies.jar
```

### Saga Verification

```sql
insert into accounts values('AAA', 'Jimmy Best');
insert into orders values('AAA', 150, 'Item0', 'A123', 'Jimmy Best', 'Transfer funds', '2020-04-22 03:19:51');
insert into orders values('AAA', -110, 'Item1', 'A123', 'amazon.it', 'Purchase', '2020-04-22 03:19:55');
insert into orders values('AAA', -100, 'Item2', 'A123', 'ebike.com', 'Purchase', '2020-04-22 03:19:58');

select * from orders_tx where account_id='AAA' and order_id='A123';
```

```java
Order Action:{"TX_ID": "TX_AAA_A123", "TX_ACTION": 0, "ACCOUNT": "AAA", "ITEMS": ["Item0"], "ORDER": "A123"}
Order Action:{"TX_ID": "TX_AAA_A123", "TX_ACTION": 0, "ACCOUNT": "AAA", "ITEMS": ["Item0", "Item1"], "ORDER": "A123"}
Order Action:{"TX_ID": "TX_AAA_A123", "TX_ACTION": -1, "ACCOUNT": "AAA", "ITEMS": ["Item0", "Item1", "Item2"], "ORDER": "A123"}
 --> compensate:{"TX_ID": "TX_AAA_A123", "TX_ACTION": -1, "ACCOUNT": "AAA", "ITEMS": ["Item0", "Item1", "Item2", "ORDER": "A123"}
```

## Kafka commands

### Create a topic:  

```bash
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic <topic_name> --partitions <number> --replication-factor <number>
```
