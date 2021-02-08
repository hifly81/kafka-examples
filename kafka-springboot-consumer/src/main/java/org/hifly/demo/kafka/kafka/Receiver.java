package org.hifly.demo.kafka.kafka;

import org.hifly.demo.kafka.model.Order;
import org.hifly.demo.kafka.mongo.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class Receiver {

    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private OrderRepository orderRepository;


    @KafkaListener(topics = "${topic-name}")
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


}