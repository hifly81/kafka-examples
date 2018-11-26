package com.redhat.kafka.shipment.store;

import com.redhat.kafka.shipment.model.Shipment;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ShipmentRepository extends PagingAndSortingRepository<Shipment, Long> {
}
