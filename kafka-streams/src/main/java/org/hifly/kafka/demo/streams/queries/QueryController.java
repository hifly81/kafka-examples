package org.hifly.kafka.demo.streams.queries;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

public class QueryController {

    public static void queryAllKeys (KafkaStreams streams, String storeName) {
        while(true) {
            try {
                StoreQueryParameters<ReadOnlyKeyValueStore<Object, Object>> storeQueryParameters = StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore());
                ReadOnlyKeyValueStore<Object, Object> keyValueStore = streams.store(storeQueryParameters);

                //all keys
                KeyValueIterator<Object, Object> range = keyValueStore.all();
                while (range.hasNext()) {
                    KeyValue<Object, Object> next = range.next();
                    System.out.println("query result " + next.key + ": " + next.value);
                }
                Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
