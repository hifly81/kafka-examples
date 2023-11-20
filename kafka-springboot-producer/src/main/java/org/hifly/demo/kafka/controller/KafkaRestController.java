package org.hifly.demo.kafka.controller;

import org.hifly.demo.kafka.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaRestController {

	Logger logger = LoggerFactory.getLogger(KafkaRestController.class);

	private final Producer producer;

	@Autowired
	KafkaController(Producer producer) {
		this.producer = producer;
	}

	@PostMapping(value="/api/order")
	public ResponseEntity send(@RequestBody Order order) {
		logger.info("sending order to kafka: {0}", order);
		this.producer.send(order.toString());
		return new ResponseEntity<>(HttpStatus.OK);
	}


}