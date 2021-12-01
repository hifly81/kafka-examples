package org.hifly.kafka.demo.streams.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.hifly.kafka.demo.streams.CarInfo;
import org.hifly.kafka.demo.streams.CarSensor;
import org.hifly.kafka.demo.streams.SpeedInfo;
import org.hifly.kafka.demo.streams.serializer.SpeedInfoDeserializer;
import org.hifly.kafka.demo.streams.serializer.SpeedInfoSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class CarSensorStream {

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null ? System.getenv("kafka.broker.list") : "localhost:9092,localhost:9093,localhost:9094";

    private static final double SPEED_LIMIT = 150.0;

    public static void main (String [] args ) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "carsensor_app_id");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams-carsensors");

        final String carSensorTopic = "carsensor-topic";
        final String carInfoTopic = "carinfo-topic";
        final String outputTopic = "output-topic";

        CarSensorStream carSensorStream = new CarSensorStream();

        Topology topology = carSensorStream.createTopology(carSensorTopic, carInfoTopic, outputTopic);
        System.out.println(topology.describe());

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);

        // SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                kafkaStreams.close();
                latch.countDown();
            } catch (final Exception e) {
            }
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
        final Serde<SpeedInfo> speedInfoSerde = Serdes.serdeFrom(speedInfoSerializer, speedInfoDeserializer);

        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapper mapper = new ObjectMapper();

        //create a ktable from car info
        KTable<String, String> carInfo = builder.table(carInfoTopic);
        KTable<String, CarInfo> carInfoTable = carInfo
                .mapValues(car -> {
                    try {
                        return mapper.readValue(car, CarInfo.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Can't generate the ktable" + e);
                    }
                });

        //TODO change value of kstream to CarSensor
        //create a kstream from car sensor data but extract only the speed data > speed limit
        KStream<String, CarSensor> streamCarSensor = builder.stream(
                carSensorTopic,
                Consumed.as("CarSensor_input_topic").with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> System.out.println("Incoming record - key " +key +" value " + value))
                .filter((s, s2) -> {
                    try {
                        CarSensor carSensor = mapper.readValue(s2, CarSensor.class);
                        return carSensor.getSpeed() > SPEED_LIMIT;
                    } catch (Exception e) {
                        throw new RuntimeException("Can't generate the kstream" + e);
                    }

                }, Named.as("CarSensor_filter_speed_limit"))
                .mapValues(car -> {
                    try {
                        CarSensor carSensor = mapper.readValue(car, CarSensor.class);
                        return carSensor;
                        //return Float.toString(carSensor.getSpeed());
                    } catch (Exception e) {
                        throw new RuntimeException("Can't generate the kstream" + e);
                    }
                }, Named.as("CarSensor_map_car_sensor"));


        // join kstream and ktable
        KStream<String, SpeedInfo> speedInfo = streamCarSensor.leftJoin(carInfoTable,
                (speed, car) -> new SpeedInfo(speed.getSpeed(), car));

        //publish to output topic
        speedInfo.peek((key, value) -> System.out.println("Outgoing record - key " +key +" value " + value));
        speedInfo.to(outputTopic, Produced.as("CarSensor_output_topic").with(Serdes.String(), speedInfoSerde));

        return builder.build();

    }


}
