package com.redhat.kafka.shipment.controller;

import com.redhat.kafka.shipment.event.OrderEvent;
import com.redhat.kafka.shipment.model.Order;
import com.redhat.kafka.shipment.model.Shipment;
import com.redhat.kafka.shipment.store.InMemoryStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/process-store")
public class ShipmentController {

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity receiveOrder(@RequestBody OrderEvent orderEvent) {
		Map<String, List> store = InMemoryStore.getStore();;
		if(store.containsKey(orderEvent.getId())) {
			List<OrderEvent> orderEvents = store.get(orderEvent.getId());
			store.get(orderEvent.getId()).add(orderEvent);
			//base groupBy supports only 1 order per Shipment
			Map<OrderEvent.EventType, List<OrderEvent>> orderEventGrouped =
					orderEvents.stream().collect(Collectors.groupingBy(w -> w.getEventType()));
			if(orderEventGrouped.containsKey(OrderEvent.EventType.ORDER_CREATED)
					&& orderEventGrouped.containsKey(OrderEvent.EventType.ORDER_READY)) {
				//TODO create shipment and sent to output topic
				Order order = new Order();
				order.setId(orderEvent.getId());
				order.setName(orderEvent.getOrderName());
				Shipment shipment = new Shipment();
				shipment.setOrder(order);
				shipment.setCourier("Fedex");
				store.remove(orderEvent.getId());
				System.out.printf("Created shipment %s\n", shipment);
			}
		}

		else {
			List<OrderEvent> orderEvents = new ArrayList<>();
			orderEvents.add(orderEvent);
			store.put(orderEvent.getId(), orderEvents);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}


}
