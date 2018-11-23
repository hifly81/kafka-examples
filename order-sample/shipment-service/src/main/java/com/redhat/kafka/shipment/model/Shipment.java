package com.redhat.kafka.shipment.model;

import java.util.UUID;

public class Shipment {

    private String id = UUID.randomUUID().toString();
    private String courier;
    private Order order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String toString() {
        return id + "- order:" + order.getId() + "," + order.getName();
    }
}
