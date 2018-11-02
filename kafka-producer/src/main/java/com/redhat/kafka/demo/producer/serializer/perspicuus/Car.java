package com.redhat.kafka.demo.producer.serializer.perspicuus;

import java.io.Serializable;

public class Car implements Serializable {

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
