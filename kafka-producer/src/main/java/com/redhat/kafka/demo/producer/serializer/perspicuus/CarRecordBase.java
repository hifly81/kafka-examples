package com.redhat.kafka.demo.producer.serializer.perspicuus;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;

public class CarRecordBase extends SpecificRecordBase {

    private String model;
    private String brand;

    @Override
    public Schema getSchema() {
        return AvroDataProducer.getSchema();
    }

    public Object get(int field) {
        switch(field) {
            case 0:
                return this.model;
            case 1:
                return this.brand;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    public void put(int field, Object value) {
        switch(field) {
            case 0:
                this.model = (String)value;
                return;
            case 1:
                this.brand = (String)value;
                return;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }
}
