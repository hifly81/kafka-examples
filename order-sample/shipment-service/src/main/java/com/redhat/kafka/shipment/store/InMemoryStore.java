package com.redhat.kafka.shipment.store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore {

    private static Map<String, List> store = new ConcurrentHashMap<>();

    public static Map<String, List> getStore() {
        return store;
    }


}
