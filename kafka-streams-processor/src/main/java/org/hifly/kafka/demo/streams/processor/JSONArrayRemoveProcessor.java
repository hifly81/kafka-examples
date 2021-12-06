package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONArrayRemoveProcessor implements Processor<String, String, String, String> {

    private Logger log = LoggerFactory.getLogger(JSONArrayRemoveProcessor.class);

    private ProcessorContext context;
    private String jsonFieldToRemove;

    public JSONArrayRemoveProcessor(String jsonFieldToRemove) {
        this.jsonFieldToRemove = jsonFieldToRemove;
    }

    @Override
    public void init(ProcessorContext<String, String> context) {
        Processor.super.init(context);
        this.context = context;
    }

    @Override
    public void process(Record<String, String> record) {
        String key = record.key();
        String value = record.value();
        JSONObject json = null;
        try {
            json = new JSONObject(value);
            json.remove(jsonFieldToRemove);
            Record result = new Record(key, json.toString(), System.currentTimeMillis());
            context.forward(result);
        } catch (Exception ex) {
            log.error("Can't remove field {} from json {}", jsonFieldToRemove, json !=null?json:"");

        }

        context.commit();

    }

    @Override
    public void close() {
        // close any resources managed by this processor
    }

}