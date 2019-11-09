package com.redhat.kafka.order.process.event;

import java.util.Date;
import java.util.List;

public class OrderEvent {

    public enum EventType {
        ORDER_CREATED, ORDER_READY, ORDER_ITEM_READY;
    }

    private String id;
    private String name;
    private Date timestamp;
    private EventType eventType;
    private List<String> itemIds;
    private ItemEvent itemEvent;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }

    public ItemEvent getItemEvent() {
        return itemEvent;
    }

    public void setItemEvent(ItemEvent itemEvent) {
        this.itemEvent = itemEvent;
    }

    public String toString() {
        return "OrderEvent: " + id + "-" + eventType + "-" + timestamp;
    }

}
