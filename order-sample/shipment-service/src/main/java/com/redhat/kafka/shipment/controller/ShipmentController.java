package com.redhat.kafka.shipment.controller;

import com.redhat.kafka.shipment.event.ItemEvent;
import com.redhat.kafka.shipment.event.OrderEvent;
import com.redhat.kafka.shipment.model.Order;
import com.redhat.kafka.shipment.model.OrderItem;
import com.redhat.kafka.shipment.model.Shipment;
import com.redhat.kafka.shipment.store.InMemoryStore;
import com.redhat.kafka.shipment.store.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/shipment")
public class ShipmentController {

    @Autowired
    private ShipmentRepository shipmentRepository;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity receiveOrder(@RequestBody OrderEvent orderEvent) {

        System.out.printf("Received order event:" + orderEvent + "\n");

        Map<String, List> store = InMemoryStore.getStore();
        if (orderEvent.getEventType() == OrderEvent.EventType.ORDER_CREATED) {
            List events = new ArrayList();
            events.add(orderEvent);
            store.put(orderEvent.getId(), events);
        } else if (orderEvent.getEventType() == OrderEvent.EventType.ORDER_ITEM_READY) {
            store.get(orderEvent.getId()).add(orderEvent);
        } else if (orderEvent.getEventType() == OrderEvent.EventType.ORDER_READY) {
            //evaluate if a shipment can be created
            if (store.containsKey(orderEvent.getId())) {
                List<OrderEvent> orderEvents = store.get(orderEvent.getId());
                store.get(orderEvent.getId()).add(orderEvent);
                //base groupBy
                Map<OrderEvent.EventType, List<OrderEvent>> orderEventGrouped =
                        orderEvents.stream().collect(Collectors.groupingBy(w -> w.getEventType()));
                if (orderEventGrouped.containsKey(OrderEvent.EventType.ORDER_CREATED)
                        && orderEventGrouped.containsKey(OrderEvent.EventType.ORDER_READY)
                        && orderEventGrouped.containsKey(OrderEvent.EventType.ORDER_ITEM_READY)) {
                    OrderEvent created = orderEventGrouped.get(OrderEvent.EventType.ORDER_CREATED).get(0);
                    OrderEvent ready = orderEventGrouped.get(OrderEvent.EventType.ORDER_READY).get(0);
                    if (created.getId().equals(ready.getId()) && created.getItemIds().equals(ready.getItemIds())) {
                        //check orderitem events
                        int itemsExpectedSize = created.getItemIds().size();
                        String orderId = created.getId();
                        List<OrderEvent> orderEventsWithItemReady = orderEventGrouped.get(OrderEvent.EventType.ORDER_ITEM_READY);
                        if (orderEventsWithItemReady != null && !orderEventsWithItemReady.isEmpty() && orderEventsWithItemReady.size() == itemsExpectedSize) {
                            Order order = new Order();
                            order.setId(orderEvent.getId());
                            order.setName(orderEvent.getName());
                            List<OrderItem> orderItems = new ArrayList<>();
                            double itemsPrice = 0;

                            for (OrderEvent orderEventWithItemReady : orderEventsWithItemReady) {
                                if (!orderEventWithItemReady.getItemEvent().getOrderId().equals(orderId)) {
                                    System.out.printf("Can't create shipment, item not included in this order: %s\n", orderEventWithItemReady.getId());
                                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                                }
                                else {
                                    OrderItem orderItem = new OrderItem();
                                    ItemEvent itemEvent = orderEventWithItemReady.getItemEvent();
                                    orderItem.setId(itemEvent.getId());
                                    orderItem.setName(itemEvent.getName());
                                    orderItem.setOrder(order);
                                    orderItem.setPrice(itemEvent.getPrice());
                                    orderItems.add(orderItem);
                                    itemsPrice+=itemEvent.getPrice();
                                }
                            }
                            order.setItems(orderItems);
                            Shipment shipment = saveShipment(order, itemsPrice);
                            System.out.printf("Created shipment %s with %d items\n", shipment, shipment.getOrder().getItems().size());


                        }

                    }
                }
            }


        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Shipment saveShipment(Order order, double itemsPrice) {
        Map<String, List> store = InMemoryStore.getStore();
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setCourier("Fedex");
        shipment.setPrice(15);
        shipment.setTotalPrice(itemsPrice + shipment.getPrice());
        //remove from map
        store.remove(order.getId());
        //save to DBMS
        shipmentRepository.save(shipment);
        return shipment;
    }

}
