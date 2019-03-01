package com.redhat.kafka.demo.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class CarSensorStream {

    private StreamsConfig streamsConfig;

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092,localhost:9093,localhost:9094";

    public void start(Properties properties) {
        if(properties.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG) == null)
            properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "uppercase_app_id");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        streamsConfig = new StreamsConfig(properties);
    }

    public void stream(
            String sourceTopic,
            String outputTopic,
            int delay) throws Exception {

        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapper mapper = new ObjectMapper();
        KStream<String, String> stream = builder.stream(
                sourceTopic,
                Consumed.with(Serdes.String(), Serdes.String()))
                .map((carId, car) -> {
                    try {
                        CarSensor carSensor = mapper.readValue(car, CarSensor.class);
                        return new KeyValue<>(carId, Float.toString(carSensor.getSpeed()));
                    } catch (Exception e) {
                        throw new RuntimeException("Can't generate the kstream" + e);
                    }
                });
        stream.to(outputTopic);

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(),streamsConfig);

        kafkaStreams.start();

        Thread.sleep(delay);

        kafkaStreams.close();

    }
}
