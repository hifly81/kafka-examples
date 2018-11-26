package com.redhat.kafka.shipment;

import com.redhat.kafka.shipment.event.OrderEvent;
import com.redhat.kafka.shipment.store.InMemoryStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ShipmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipmentApplication.class, args);
		verifyOrders();
	}


	public static  void verifyOrders() {
		Runnable runnable = () -> {
			while(true) {
				System.out.printf("Entries in MAP:\n");
				Map<String, List> store = InMemoryStore.getStore();
				for (Map.Entry<String, List> entry : store.entrySet()) {
					StringBuilder stringBuilder = new StringBuilder();
					if(entry.getValue() != null && !entry.getValue().isEmpty()) {
						for(Object obj: entry.getValue()) {
							OrderEvent orderEvent = (OrderEvent)obj;
							stringBuilder.append(orderEvent.getId() + "-" + orderEvent.getEventType() + "/");
						}
						System.out.printf(entry.getKey() + "/" + entry.getValue().size() + "/" + stringBuilder.toString() + "\n");
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		Thread t = new Thread(runnable);
		t.start();
	}

}
