package com.redhat.kafka.order;

import com.redhat.kafka.order.controller.OrderController;
import com.redhat.kafka.order.model.Order;

import java.util.Random;
import java.util.UUID;

public class OrderApp {

    private static final String CHAR_LIST =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 6;

    public static void main (String [] args) throws Exception {
        OrderController orderController = new OrderController();

        for(int i = 0; i < 50; i++) {
            String uuid = UUID.randomUUID().toString();
            Order order = new Order();
            order.setId(uuid);
            order.setName(generateRandomString());
            orderController.create(order);

            Thread.sleep(1000);

            orderController.ready(uuid);
        }

    }

    private static int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    private static String generateRandomString(){
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }


}
