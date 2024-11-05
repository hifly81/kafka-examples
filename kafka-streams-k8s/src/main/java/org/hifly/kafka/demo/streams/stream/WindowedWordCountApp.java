package org.hifly.kafka.demo.streams.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class WindowedWordCountApp {

    private static final Logger logger = LoggerFactory.getLogger(WindowedWordCountApp.class);

    private static final String KAFKA_BOOTSTRAP_SERVERS_ENV = "KAFKA_BOOTSTRAP_SERVERS";
    private static final String APPLICATION_ID_ENV = "APPLICATION_ID";
    private static final String STATE_DIR_ENV = "STATE_DIR";
    private static final String PROCESSING_GUARANTEE_CONFIG_ENV = "PROCESSING_GUARANTEE_CONFIG";
    private static final String AUTO_OFFSET_RESET_CONFIG_ENV = "AUTO_OFFSET_RESET_CONFIG";
    private static final String NUM_STREAM_THREADS_CONFIG_ENV = "NUM_STREAM_THREADS_CONFIG";
    private static final String NUM_STANDBY_REPLICAS_CONFIG_ENV = "NUM_STANDBY_REPLICAS_CONFIG";
    private static final String MAX_POLL_INTERVAL_MS_CONFIG_ENV = "MAX_POLL_INTERVAL_MS_CONFIG";
    private static final String SESSION_TIMEOUT_MS_CONFIG_ENV = "SESSION_TIMEOUT_MS_CONFIG";
    private static final String HEARTBEAT_INTERVAL_MS_CONFIG_ENV = "HEARTBEAT_INTERVAL_MS_CONFIG";

    private static final String INPUT_TOPIC_NAME_ENV = "INPUT_TOPIC_NAME";
    private static final String OUTPUT_TOPIC_NAME_ENV = "OUTPUT_TOPIC_NAME";

    public static void main(String[] args)  {
        Properties properties = getProperties();

        final String inputTopic = System.getenv(INPUT_TOPIC_NAME_ENV) != null ? System.getenv(INPUT_TOPIC_NAME_ENV) : "words-input-topic";
        final String outputTopic = System.getenv(OUTPUT_TOPIC_NAME_ENV) != null ? System.getenv(OUTPUT_TOPIC_NAME_ENV) : "words-counter-output-topic";

        WindowedWordCountApp streamCounter = new WindowedWordCountApp();

        Topology topology = streamCounter.createTopology(inputTopic, outputTopic);
        logger.info(topology.describe().toString());

        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {

            final CountDownLatch latch = new CountDownLatch(1);

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
        }
        System.exit(0);
    }

    public Topology createTopology(
            String inputTopic,
            String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> inputStream = builder.stream(inputTopic);

        KTable<Windowed<String>, Long> windowedCounts = inputStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count(Materialized.<String, Long>as(Stores.persistentWindowStore(
                                "rocksdb-windowed-count-store",
                                Duration.ofMinutes(5),
                                Duration.ofMinutes(1),
                                false))
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()));

        windowedCounts.toStream().to(outputTopic, Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class, 1), Serdes.Long()));

        return builder.build();

    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV) != null ? System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV) : "localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, System.getenv(APPLICATION_ID_ENV) != null ? System.getenv(APPLICATION_ID_ENV) : "wordscounter_app");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, System.getenv(PROCESSING_GUARANTEE_CONFIG_ENV) != null ? System.getenv(PROCESSING_GUARANTEE_CONFIG_ENV) : "exactly_once_v2");
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, System.getenv(NUM_STREAM_THREADS_CONFIG_ENV) != null ? System.getenv(NUM_STREAM_THREADS_CONFIG_ENV) : 2);
        properties.put(StreamsConfig.STATE_DIR_CONFIG, System.getenv(STATE_DIR_ENV) != null ? System.getenv(STATE_DIR_ENV) : "/tmp/streams-wordscounter");
        properties.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, System.getenv(NUM_STANDBY_REPLICAS_CONFIG_ENV) != null ? System.getenv(NUM_STANDBY_REPLICAS_CONFIG_ENV) : 2);

        //Consumer props
        properties.put(StreamsConfig.consumerPrefix(AUTO_OFFSET_RESET_CONFIG), System.getenv(AUTO_OFFSET_RESET_CONFIG_ENV) != null ? System.getenv(AUTO_OFFSET_RESET_CONFIG_ENV) : "earliest");
        properties.put(StreamsConfig.consumerPrefix(MAX_POLL_INTERVAL_MS_CONFIG), System.getenv(MAX_POLL_INTERVAL_MS_CONFIG_ENV) != null ? System.getenv(MAX_POLL_INTERVAL_MS_CONFIG_ENV) : 300000);
        properties.put(StreamsConfig.consumerPrefix(SESSION_TIMEOUT_MS_CONFIG), System.getenv(SESSION_TIMEOUT_MS_CONFIG_ENV) != null ? System.getenv(SESSION_TIMEOUT_MS_CONFIG_ENV) : 45000);
        properties.put(StreamsConfig.consumerPrefix(HEARTBEAT_INTERVAL_MS_CONFIG), System.getenv(HEARTBEAT_INTERVAL_MS_CONFIG_ENV) != null ? System.getenv(HEARTBEAT_INTERVAL_MS_CONFIG_ENV) : 3000);

        return properties;
    }

}

