package org.hifly.kafka.demo.streams.stream;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.hifly.kafka.demo.streams.domain.CarInfo;
import org.hifly.kafka.demo.streams.serializer.CarInfoDeserializer;
import org.hifly.kafka.demo.streams.serializer.CarInfoSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class CarBrandStream {

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null ? System.getenv("kafka.broker.list") : "localhost:9091,localhost:9092";

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "carbrand_app");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams-cars");

        final String inputTopic = "cars-input-topic";
        final String ferrariTopic = "ferrari-input-topic";
        final String carsTopic = "cars-output-topic";

        CarBrandStream carBrandStream = new CarBrandStream();
        List<NewTopic> topics = Arrays.asList(
                new NewTopic(inputTopic, Optional.of(2), Optional.empty()),
                new NewTopic(ferrariTopic, Optional.empty(), Optional.empty()),
                new NewTopic(carsTopic, Optional.empty(), Optional.empty()));

        StreamUtils.createTopics(properties, topics);

        Topology topology = carBrandStream.createTopology(inputTopic, ferrariTopic, carsTopic);
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
            String inputTopic,
            String ferrariTopic,
            String carsTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        final Serializer<CarInfo> carInfoSerializer = new CarInfoSerializer();
        final Deserializer<CarInfo> carInfoDeserializer = new CarInfoDeserializer();
        final Serde<CarInfo> carInfoSerde = Serdes.serdeFrom(carInfoSerializer, carInfoDeserializer);

        builder.stream(inputTopic, Consumed.as("Cars_input_topic").with(Serdes.String(), carInfoSerde))
                .peek((key, value) -> System.out.println("Incoming record - key " +key +" value " + value))
                //split into 2 different output topics
                .split()
                .branch(
                        (key, value) -> "Ferrari".equalsIgnoreCase(value.getBrand()),
                        Branched.withConsumer(ks -> ks.to(ferrariTopic, Produced.as("Ferrari_output_topic").with(Serdes.String(), carInfoSerde))))
                .branch(
                        (key, value) -> true,
                        Branched.withConsumer(ks -> ks.to(carsTopic, Produced.as("Cars_output_topic").with(Serdes.String(), carInfoSerde))));

        return builder.build();
    }

}
