package com.redhat.kafka.demo.producer.serializer;

import java.io.Serializable;

public class CustomData implements Serializable {

    private Integer index;

    public CustomData() {}

    public CustomData(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
