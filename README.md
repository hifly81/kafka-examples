# Examples with Apache Kafka


### Apache Kafka installation ###

A base cluster with a minimum of 1 broker (suggested 3) is need to test the examples; for details about the installation, info at:<br>
https://kafka.apache.org/documentation/#quickstart

Examples are tested with Apacha Kafka version:
2.12-2.0.0

### Apache Kafka installation on Kubernetes and OpenShift ###

If you want to run your kafka cluster on Kubernetes or OpenShift, have a look at Strimzi project:<br>
https://strimzi.io/

### Kafka producers ###

Some implementations of kafka producers.

kafka producers available:
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

Execute tests:
```
cd kafka-producer
mvn clean test
```

Every producer implementation has its own *Runner* java class producing a bunch of sample messages.
At least a kafka broker listening on port 9092 is required.

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


### Kafka consumers ###

Implementation of a kafka consumer that can be used with variuos deserializer classes.

Execute tests:
```
cd kafka-consumer
mvn clean test
```

Every consumer implementation has its own *Runner* java class consuming a bunch of sample messages.
At least a kafka broker listening on port 9092 is required.

```
cd kafka-consumer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.consumer.base.Runner"
```

### Kafka streams ###

Implementation of a series of kafka streams topoligies to analyze car data.<br>

CarSensorStream:<br>
The stream filters out speed data from car data sensor records. Speed limit is set to 150km/h and only events exceeding the limits are filtered out<br>
A ktable stores the car info data.<br>
A left join between the kstream and the ktable produces a new aggregated object published to an output topic.

CarBrandStream:<br>
The stream splits the original data into 2 different topics, one for Ferrari cars and one for all other car brands.

Execute tests:
```
cd kafka-streams
mvn clean test
```

### Kafka commands ###

Create a topic:

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic <topic_name> --partitions <number> --replication-factor <number>
```
