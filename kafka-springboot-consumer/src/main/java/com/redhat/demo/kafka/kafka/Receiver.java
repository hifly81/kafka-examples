package com.redhat.demo.kafka.kafka;

import com.redhat.demo.kafka.model.Order;
import com.redhat.demo.kafka.mongo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class Receiver {

    @Autowired
    private OrderRepository orderRepository;


    @KafkaListener(topics = "${topic-name}")
    public void listen(@Payload String message) {

        System.out.println("received message:" + message);

        Order order = new Order();

        try {
            //generate random id
            order.setId(ThreadLocalRandom.current().nextLong(10000000));
            order.setName(message);

            System.out.println("order saving to mongo...:" + order);

            //save to mongo
            orderRepository.save(order);
            System.out.println("order saved to mongo:" + order);
        } catch(Exception ex) {
            System.out.println("can't save to mongo:" + order);
        }
    }

}