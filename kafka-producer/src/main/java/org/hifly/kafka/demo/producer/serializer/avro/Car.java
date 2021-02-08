package org.hifly.kafka.demo.producer.serializer.avro;

import java.io.Serializable;

public class Car implements Serializable {

    private static final long serialVersionUID = 6214326742498643495L;

    private String model;

    private String brand;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
