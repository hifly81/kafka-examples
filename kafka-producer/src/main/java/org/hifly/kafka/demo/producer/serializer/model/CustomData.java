package org.hifly.kafka.demo.producer.serializer.model;

import java.io.Serializable;

public class CustomData implements Serializable {

    private static final long serialVersionUID = 1L;
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
