package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ExpiredMessagesProcessor implements Processor<String, String, String, String> {


    private Logger log = LoggerFactory.getLogger(ExpiredMessagesProcessor.class);
    private ProcessorContext<String, String> context;
    private KeyValueStore<String, String> store;

    final static long SECONDS_SESSION_EXPIRED = 30l;
    final static int PUNCTUATE_INTERVAL = 15;
    final static int HOURS_CORRECTION = 2;

    @Override
    public void init(final ProcessorContext<String, String> context) {
        this.context = context;
        this.store = context.getStateStore(ExpiredMessagesApplication.STATE_STORE_NAME);
    }

    @Override
    public void process(Record<String, String> record) {
        //Key not present in store
        if (store.get(record.key()) == null) {
            store.put(record.key(), record.value());
            RemoveEntry removeEntry = new RemoveEntry();
            // schedule a punctuate() method every PUNCTUATE_INTERVAL seconds based on stream-time
            Cancellable cancelTask = this.context.schedule(Duration.ofSeconds(PUNCTUATE_INTERVAL), PunctuationType.WALL_CLOCK_TIME, removeEntry);
            removeEntry.setCancellable(cancelTask);

            context.forward(record);
        }

        context.commit();
        context.forward(record);
    }

    @Override
    public void close() {}

    private class RemoveEntry implements Punctuator {

        private Cancellable cancellable;

        public void setCancellable(Cancellable cancellable) {
            this.cancellable = cancellable;
        }

        @Override
        public void punctuate(long timestamp) {
            //Iterate over Store entries
            KeyValueIterator<String, String> iter = store.all();
            while (iter.hasNext()) {
                KeyValue<String, String> entry = iter.next();
                deleteEntryIfExpired(entry, cancellable);
            }
            iter.close();
            context.commit();
        }
    }

    private void deleteEntryIfExpired(KeyValue<String, String> entry, Cancellable cancellable) {
        //if the entry is older than XX remove from the store
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date tempDate;
        try {
            JSONObject json = new JSONObject(entry.value);
            String timestamp = json.getString("time");
            tempDate = input.parse(timestamp);
            LocalDateTime from = LocalDateTime.ofInstant(tempDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime to = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusHours(HOURS_CORRECTION);
            Duration duration = Duration.between(from, to);
            if (duration.toSeconds() > SECONDS_SESSION_EXPIRED) {
                log.info(entry.key + " is expired --> Sessions between:" + duration.toMinutes());
                store.delete(entry.key);
                cancellable.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}