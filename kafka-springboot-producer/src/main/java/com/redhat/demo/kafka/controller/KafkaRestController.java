package com.redhat.demo.kafka.controller;

import com.redhat.demo.kafka.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaRestController {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value("${topic-name}")
	private String TOPIC_NAME;

	@PostMapping(value="/api/order")
	public ResponseEntity send(@RequestBody Order order) {
		kafkaTemplate.send(TOPIC_NAME, order.toString());
		return new ResponseEntity<>(HttpStatus.OK);
	}


	

}