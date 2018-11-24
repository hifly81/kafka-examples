package com.redhat.kafka.shipment.model;

import java.util.UUID;

public class Shipment {

    private String id = UUID.randomUUID().toString();
    private String courier;
    private Order order;
    private double price;
    private double totalPrice;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String toString() {
        return id + "- order:" + order.getId() + "," + order.getName() +"," + price + "," + totalPrice;
    }

}
