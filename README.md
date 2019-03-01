# Examples with Apache Kafka


### Apache Kafka installation ###

A base cluster with a minimum of 1 broker (suggested 3) is need to test the examples; for details about the installation, info at:<br>
https://kafka.apache.org/documentation/#quickstart

Examples are tested with Apacha Kafka version:
2.12-2.0.0

### Apache Kafka installation on Kubernetes and OpenShift###

If you want to run your kafka cluster on Kubernetes or OpenShift, have a look at Strimzi project:<br>
https://strimzi.io/


### Order application ###

A sample application showing how to send and receive events to/from kafka.
The application simulates the creation of an Order and a Shipment: <br>
 - an Order contains several OrderItem
 - a Shipment can be created only when all items beloging to an order are READY.

The Order events are sent to a Kafka topic via Order service.<br>
The Order events are received by Order process service and sent to the Shipment service via a Rest call.<br>
The Shipment service aggregates the events and produces a Shipment object. The Shipment object is also saved on DBMS.

Create a postgres db schema for the Shipment: (info for a postgres installed on a Linux machine)
```
su - postgres
CREATE DATABASE orders;
CREATE USER orders WITH PASSWORD 'orders';
GRANT ALL PRIVILEGES ON DATABASE orders to orders;
```
Modify property spring.datasource.url inside the file application.properties with your postgres host and port.


Run the shipment service: (aggregates kafka event and creates the Shipment when an Order is READY)
```
cd order-sample/shipment-service
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.shipment.ShipmentApplication"
```

Run the order process service: (receive kafka events)
```
cd order-sample/order-process-service
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.order.process.OrderProcessApp"
```

Run the order service: (send kafka events)
```
cd order-sample/order-service
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.order.OrderApp"
```

### Order application - run on OpenShift ###

- Required software

- You need OpenShift 3.11 and a user with cluster-admin role (to deploy the strimzi operators)
- You will run the Apache Kafka (and Zookeeper) cluster on OpenShift using Strmizi:
https://strimzi.io
- You will also deploy on Openshift, Promethes and Grafana to collect and show some kafka metrics
- Shipment service will de deployed on OpenShift using the fabric8 maven plugin
https://maven.fabric8.io/
- Order process service will be deployed on OpenShift using the openjdk18 image for OpenShift
https://registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift
- Order service will be deployed on OpenShift using the openjdk18 image for OpenShift and running (on demand) and OpenShift job that will run
a scenario with 500 orders.

This is an image showing your final namespace:
![ScreenShot 1](order-sample/images/myproject.png)

- Download strimzi, version 0.10

```
wget https://github.com/strimzi/strimzi-kafka-operator/releases/download/0.10.0/strimzi-0.10.0.tar.gz
tar xvf strimzi-0.10.0.tar.gz
```

- Create an OpenShift project and kafka cluster with 3 brokers (3 zookeeper)

```
oc login -u <user> -p <password>
oc new-project myproject
oc apply -f $STRIMZI_HOME/install/cluster-operator -n myproject
oc apply -f $STRIMZI_HOME/examples/templates/cluster-operator -n myproject
oc apply -f $STRIMZI_HOME/examples/metrics/kafka-metrics.yaml -n myproject
```

- Optional, deploy kafka connect and test a sample producer and a sample consumer

```
oc apply -f $STRIMZI_HOME/examples/kafka-connect/kafka-connect.yaml -n myproject
oc run kafka-producer -ti --image=strimzi/kafka:0.10.0-kafka-2.1.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
oc run kafka-consumer -ti --image=strimzi/kafka:0.10.0-kafka-2.1.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```

- Install strimzi metrics, prometheus and grafana

```
wget https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.10.0/metrics/examples/prometheus/kubernetes.yaml
mv kubernetes.yaml prometheus.yaml
oc apply -f prometheus.yaml -n myproject
wget https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.10.0/metrics/examples/grafana/kubernetes.yaml
mv kubernetes.yaml grafana.yaml
oc apply -f grafana.yaml -n myproject
oc port-forward <name-of-grafana-pod> 3000:3000
```

- Import grafana dashboards

Login to http://localhost:3000 (admin/admin) and follow the steps available at:<br>
https://strimzi.io/docs/latest/#grafana_dashboard

Grafana kafka dashboard:

![ScreenShot 2](order-sample/images/grafana.png)

- Deploy a container for postgres (required by the shipment service)

```
oc import-image my-rhscl/postgresql-96-rhel7 --from=registry.access.redhat.com/rhscl/postgresql-96-rhel7 --confirm -n myproject
oc new-app -e POSTGRESQL_USER=orders -e POSTGRESQL_PASSWORD=orders -e POSTGRESQL_DATABASE=orders postgresql-96-rhel7 -n myproject
```

- Deploy a container for the shipment service (we will use fabric8 maven plugin)

```
cd shipment-service
mvn package fabric8:deploy -Popenshift -DskipTests
```

- Deploy a container for the order process service (we will use source to image build in openshift)

```
cd order-process-service
oc import-image my-redhat-openjdk-18/openjdk18-openshift --from=registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift --confirm -n myproject
oc new-build --binary --image-stream openjdk18-openshift --name order-process-service
oc start-build order-process-service --from-dir=.
oc new-app order-process-service -e kafka.broker.list=my-cluster-kafka-bootstrap:9092 -e shipment.url=http://shipment-service:8080/shipment
```

- Deploy a container for the order service (we will use source to image build in openshift)

```
cd order-service
oc new-build --binary --image-stream openjdk18-openshift --name order-service
oc start-build order-service --from-dir=.
```

- Run a simulated scenario (send 500 orders)

An OpenShift job invoking the main java class for the Order Service will be created and set of 500 will be delivered.

```
cd order-service/src/main/resources
oc create -f orderservicejob.yml
```


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

Implementation of a kafka stream to analyze car sensors.

Execute tests:
```
cd kafka-streams
mvn clean test
```

Every consumer implementation has its own *Runner* java class consuming a bunch of sample messages.
At least a kafka broker listening on port 9092 is required.

```
cd kafka-consumer
mvn clean compile && mvn exec:java -Dexec.mainClass="com.redhat.kafka.demo.consumer.base.Runner"
```


### Kafka commands ###

Create a topic:

```
export KAFKA_OPTS="-Djava.security.auth.login.config=../configs/kafka/jaas.config"
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic <topic_name> --partitions <number> --replication-factor <number>
```
