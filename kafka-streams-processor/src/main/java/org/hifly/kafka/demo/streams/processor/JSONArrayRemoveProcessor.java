package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONArrayRemoveProcessor implements Processor<String, String> {

    private Logger log = LoggerFactory.getLogger(JSONArrayRemoveProcessor.class);

    private ProcessorContext context;
    private String jsonFieldToRemove;

    public JSONArrayRemoveProcessor(String jsonFieldToRemove) {
        this.jsonFieldToRemove = jsonFieldToRemove;
    }


    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(String key, String value) {
        JSONObject json = null;
        try {
            json = new JSONObject(value);
            json.remove(jsonFieldToRemove);
            context.forward(key, json.toString());
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