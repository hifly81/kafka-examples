package com.redhat.demo.kafka.mongo;

import com.redhat.demo.kafka.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
 

public interface OrderRepository extends MongoRepository<Order, Long> {

}