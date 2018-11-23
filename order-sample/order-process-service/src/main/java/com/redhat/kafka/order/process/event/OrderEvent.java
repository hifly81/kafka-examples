package com.redhat.kafka.order.process.event;

import java.io.Serializable;
import java.util.Date;

public class OrderEvent implements Serializable {

    public enum EventType {
        ORDER_CREATED, ORDER_READY;
    }

    private String id;
    private String orderName;
    private Date timestamp;
    private EventType eventType;


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String toString() {
        return "OrderEvent: " + id + "-" + eventType + "-" + timestamp;
    }

}
