package org.hifly.demo.kafka.kafka;

import org.hifly.demo.kafka.model.Order;
import org.hifly.demo.kafka.mongo.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class Receiver {

    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private OrderRepository orderRepository;


    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            exclude = NullPointerException.class)
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload String message) {

        logger.info("received message:" + message);

        Order order = new Order();

        //generate random id
        order.setId(ThreadLocalRandom.current().nextLong(10000000));
        order.setName(message);

        logger.info("order is going to be saved into mongo...:" + order);

        if (order.getName().contains("ERROR-"))
            throw new OrderException();

        try {
            //save to mongo
            orderRepository.save(order);

            logger.info("order saved to mongo:" + order);
        } catch (Exception ex) {
            logger.error("can't save to mongo:" + order);
        }
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        logger.info(String.format("#### -> DLT Consumed message -> %s", record));
    }


}