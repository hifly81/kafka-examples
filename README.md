# Examples with Apache Kafka

## Apache Kafka installation
A base cluster with 3 broker is need to test the examples; fro details about the installation, info at:
https://kafka.apache.org/documentation/#quickstart

Examples are tested with Apacha Kafka version:
2.12-2.0.0

## kafka producer

Implementation of kafka producers:
  - base: uses a *org.apache.kafka.common.serialization.StringSerialize* for key and value
  - json: uses a *org.apache.kafka.common.serialization.StringSerialize* for key and a *com.redhat.kafka.demo.producer.serializer.json.JsonSerializer* for value
  - avro: uses a *io.confluent.kafka.serializers.KafkaAvroSerializer* for key and value.<br>
  A running confluent schema registry is need to register the avro schema. Further info at: https://github.com/confluentinc/schema-registry
  - perspicuus: uses a custom AvroSerializer *com.redhat.kafka.demo.producer.serializer.perspicuus.AvroSerializer* for key and value.<br>
  A running RedHat perspicuus schema registry is need to register the avro schema. Further info at: https://github.com/jhalliday/perspicuus

Every producer implementation has its own *Runner* java class producing a bunch of sample messages.
