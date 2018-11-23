package com.redhat.kafka.shipment.event;

import java.util.Date;

public class OrderEvent {

    public enum EventType {
        ORDER_CREATED, ORDER_READY;
    }

    private Date timestamp;
    private EventType eventType;
    private String id;


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

}
