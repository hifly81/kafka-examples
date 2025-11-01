package org.hifly.kafka.order.process.consumer.handle;

import java.util.Map;

import org.hifly.kafka.demo.consumer.core.AbstractConsumerHandle;
import org.hifly.kafka.order.process.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrderProcessHandle<K,V> extends AbstractConsumerHandle<K,V> {

    private Logger log = LoggerFactory.getLogger(OrderProcessHandle.class);

    private Map<TopicPartition, OffsetAndMetadata> offsets;

    @Override
    public void addOffsets(Map<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets) {
        this.offsets = offsets;
    }

    @Override
    public void process(ConsumerRecords<K, V> consumerRecords, String groupId, String consumerId) {
        for (ConsumerRecord<K, V> record : consumerRecords) {
            OrderEvent orderEvent = (OrderEvent) record.value();
            log.info("Received record from kafka {}", record.key());
            //ShipmentClient shipmentClient = new ShipmentClient();
            //shipmentClient.sendOrderEvent(REST_SHIPMENT_URI, orderEvent);
        }
    }

    @Override
    public void process(ConsumerRecords<K, V> consumerRecords, String groupId) {
        process(consumerRecords, groupId, null);
    }

}

