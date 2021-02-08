package org.hifly.demo.kafka.mongo;

import org.hifly.demo.kafka.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
 

public interface OrderRepository extends MongoRepository<Order, Long> {

}