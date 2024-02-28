package org.hifly.saga.payment;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hifly.saga.payment.model.OrderAction;
import org.hifly.saga.payment.model.OrderActionAck;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class OrderSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    private static final String TOPIC_ORDER_ACTIONS = "order_actions";
    private static final String TOPIC_ORDER_ACTIONS_ACK = "order_actions_ack";

    public static void main(final String[] args) throws IOException {
        runConsumer();
    }

    private static Properties createProducerProperties() {
     
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        return props;
    }

    private static Properties createConsumerProperties() {
        String generatedString = RandomStringUtils.random(10, true, true);

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, generatedString);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        return props;
    }

    private static void compensate(OrderAction orderAction) {
        LOGGER.info("\n --> compensate:{}", orderAction);
        OrderActionAck orderActionAck = new OrderActionAck();
        orderActionAck.setORDERID(orderAction.getORDER());
        orderActionAck.setTXID(orderAction.getTXID());
        orderActionAck.setPARTICIPIANTID("TICKET_SERVICE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(new Date());
        orderActionAck.setTIMESTAMP(formattedDate);

        //TODO rollback local store

        try (final KafkaProducer<String, OrderActionAck> producer = new KafkaProducer<>(createProducerProperties())) {
            final ProducerRecord<String, OrderActionAck> record = 
                new ProducerRecord<>(TOPIC_ORDER_ACTIONS_ACK, null, orderActionAck);
            producer.send(record);
        }
    }

    private static void runConsumer() {
        try (final KafkaConsumer<String, OrderAction> consumer = new KafkaConsumer<>(createConsumerProperties())) {
            consumer.subscribe(Collections.singletonList(TOPIC_ORDER_ACTIONS));

            while (true) {
                final ConsumerRecords<String, OrderAction> records = consumer.poll(Duration.ofMillis(100));
                for (final ConsumerRecord<String, OrderAction> record : records) {
                    final OrderAction orderAction = record.value();

                    if (orderAction != null) {
                        LOGGER.info("\nOrder Action:{}", orderAction);

                        if(orderAction.getTXACTION() == -1)
                            compensate(orderAction);

                    }
                }
            }
        }
    }

}

