package com.redhat.kafka.shipment;

import com.redhat.kafka.shipment.controller.ShipmentController;

public class ShipmentApp {

    public static void main (String [] args) {
        ShipmentController shipmentController = new ShipmentController();
        shipmentController.receiveOrders(1, -1, 10);
        shipmentController.verifyOrders();


    }






}
