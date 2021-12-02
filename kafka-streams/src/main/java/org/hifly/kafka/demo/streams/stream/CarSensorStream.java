package org.hifly.kafka.demo.streams.stream;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.hifly.kafka.demo.streams.CarInfo;
import org.hifly.kafka.demo.streams.CarSensor;
import org.hifly.kafka.demo.streams.SpeedInfo;
import org.hifly.kafka.demo.streams.serializer.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CarSensorStream {

    private static final Logger logger = LoggerFactory.getLogger(CarSensorStream.class);

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null ? System.getenv("kafka.broker.list") : "localhost:9092,localhost:9093,localhost:9094";

    private static final double SPEED_LIMIT = 150.0;

    public static void main (String [] args ) throws ExecutionException, InterruptedException, TimeoutException {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "carsensor_app");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once_v2");
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams-carsensors");

        final String carSensorTopic = "carsensor-topic";
        final String carInfoTopic = "carinfo-topic";
        final String outputTopic = "output-topic";

        CarSensorStream carSensorStream = new CarSensorStream();
        List<NewTopic> topics = Arrays.asList(
                new NewTopic(carSensorTopic, Optional.of(2), Optional.empty()),
                new NewTopic(carInfoTopic, Optional.of(2), Optional.empty()),
                new NewTopic(outputTopic, Optional.empty(), Optional.empty()));

        StreamUtils.createTopics(properties, topics);

        Topology topology = carSensorStream.createTopology(carSensorTopic, carInfoTopic, outputTopic);
        logger.info(topology.describe().toString());

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);

        // SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                kafkaStreams.close();
                latch.countDown();

            } catch (final Exception e) {}
        }));

        try {
            kafkaStreams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
        
    }

    public Topology createTopology(
            String carSensorTopic,
            String carInfoTopic,
            String outputTopic) {

        final Serializer<SpeedInfo> speedInfoSerializer = new SpeedInfoSerializer();
        final Deserializer<SpeedInfo> speedInfoDeserializer = new SpeedInfoDeserializer();
        final Serializer<CarInfo> carInfoSerializer = new CarInfoSerializer();
        final Deserializer<CarInfo> carInfoDeserializer = new CarInfoDeserializer();
        final Serializer<CarSensor> carSensorSerializer = new CarSensorSerializer();
        final Deserializer<CarSensor> carSensorDeserializer = new CarSensorDeserializer();
        final Serde<SpeedInfo> speedInfoSerde = Serdes.serdeFrom(speedInfoSerializer, speedInfoDeserializer);
        final Serde<CarInfo> carInfoSerde = Serdes.serdeFrom(carInfoSerializer, carInfoDeserializer);
        final Serde<CarSensor> carSensorSerde = Serdes.serdeFrom(carSensorSerializer, carSensorDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        //create a ktable from car info
        KTable<String, CarInfo> carInfoTable = builder.table(carInfoTopic, Consumed.with(Serdes.String(), carInfoSerde));

        //create a kstream from car sensor data but extract only the speed data > speed limit
        KStream<String, CarSensor> streamCarSensor = builder.stream(
                carSensorTopic,
                Consumed.as("CarSensor_input_topic").with(Serdes.String(), carSensorSerde))
                .peek((key, value) -> logger.info("Incoming record - key " +key +" value " + value))
                .filter((s, s2) -> s2.getSpeed() > SPEED_LIMIT, Named.as("CarSensor_filter_speed_limit"));


        // join kstream and ktable
        KStream<String, SpeedInfo> speedInfo = streamCarSensor.leftJoin(carInfoTable,
                (speed, car) -> new SpeedInfo(speed.getSpeed(), car));

        //publish to output topic
        speedInfo.peek((key, value) -> logger.info("Outgoing record - key " +key +" value " + value));
        speedInfo.to(outputTopic, Produced.as("CarSensor_output_topic").with(Serdes.String(), speedInfoSerde));

        return builder.build();

    }


}
