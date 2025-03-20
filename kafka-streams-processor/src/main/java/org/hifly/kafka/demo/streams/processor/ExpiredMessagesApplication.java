package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ExpiredMessagesApplication {

    final public static String STATE_STORE_NAME = "expired-messages-store";
    final static String APPLICATION_ID = "expired-messages-app";
    final static String INPUT_TOPIC = "expired-messages-input-topic";
    final static String OUTPUT_TOPIC = "expired-messages-output-topic";

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null ? System.getenv("kafka.broker.list") : "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put("metrics.recording.level", "DEBUG");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);

        final StreamsBuilder builder = new StreamsBuilder();

        //Create a Store
        Map<String, String> changelogConfig = new HashMap<>();
        StoreBuilder<KeyValueStore<String, String>> stateStore = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STATE_STORE_NAME),
                Serdes.String(),
                Serdes.String()).withLoggingEnabled(changelogConfig);
        builder.addStateStore(stateStore);

        builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()))
                .process(ExpiredMessagesProcessor::new, stateStore.name())
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.start();
    }


}