# Examples with Apache Kafka

## Apache Kafka installation
A base cluster with a minimum of 1 broker (suggested 3) is need to test the examples; for details about the installation, info at:<br>
https://kafka.apache.org/documentation/#quickstart

Examples are tested with Apacha Kafka version:
2.12-2.0.0

## Compile, Test, Run

If you want to run the test you need:
 - at least 1 broker running. listening on localhost, port 9092
 - a running confluent schema registry is need to register the avro schema. Further info at: https://github.com/confluentinc/schema-registry
 -  a running RedHat perspicuus schema registry is need to register the avro schema. Further info at: https://github.com/jhalliday/perspicuus
 - a topic with 3 partitions named "demo-3" must exists.

### Compile: ###

```
mvn clean compile
```

### Package and test: ###

```
mvn clean package
```

### Test: ###

```
mvn test
```


### Order sample: ###

A sample application showing how to send and receive events to/from kafka.
The application simulates the creation of an Order: <br>
 - an Order contains several OrderItem
 - a Shipment can be created only when all items beloging to an order are READY


Run the shipment service (create the Shipment when Order is READY)
```
cd order-sample/shipment-service
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.shipment.ShipmentApplication"
```

Run the order process service (receive kafka events)
```
cd order-sample/order-process-service
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.order.process.OrderProcessApp"
```

Run the order service (send kafka events)
```
cd order-sample/order-service
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.order.OrderApp"
```


### Run kafka producers: ###

Implementation of kafka producer:
  - base: uses a *org.apache.kafka.common.serialization.StringDeserializer* for key and value
  - json: uses a *org.apache.kafka.common.serialization.StringSerialize* for key and a *com.redhat.kafka.demo.producer.serializer.json.JsonSerializer* for value
  - avro: uses a *io.confluent.kafka.serializers.KafkaAvroSerializer* for key and value.<br>
  A running confluent schema registry is need to register the avro schema. <br>
  Info at: https://github.com/confluentinc/schema-registry
  - perspicuus: uses a custom AvroSerializer *com.redhat.kafka.demo.producer.serializer.perspicuus.AvroSerializer* for key and value.<br>
  A running RedHat perspicuus schema registry is need to register the avro schema.<br>
  Info at: https://github.com/jhalliday/perspicuus
  - partitioner: use a custom partitioner for keys.<br>
  A topic with 3 partitions named "demo-2" must exists.

Every producer implementation has its own *Runner* java class producing a bunch of sample messages.

```
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.producer.serializer.base.Runner"
```

```
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.producer.serializer.json.Runner"
```

```
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.producer.serializer.avro.Runner"
```

```
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.producer.serializer.perspicuus.Runner"
```

```
cd kafka-producer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.producer.partitioner.custom.Runner"
```

### Run kafka consumers: ###

Implementation of kafka consumer:
  - base: uses a *org.apache.kafka.common.serialization.StringDeserializer* for key and value

Every consumer implementation has its own *Runner* java class consuming a bunch of sample messages.

```
cd kafka-consumer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.consumer.base.Runner"
```


### Kafka commands

Create a topic:

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic <topic_name> --partitions <number> --replication-factor <number>
```
