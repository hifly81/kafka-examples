package com.redhat.kafka.demo.consumer.base;

public class Runner {

    public static void main (String [] args) throws Exception {
        pollAutoCommit();
        pollSyncCommit();
    }

    private static void pollAutoCommit() {
        for(int i = 0; i < 4; i++) {
            //one consumer will be idle with 3 partitions
            Thread t = new Thread(new ConsumerThread(String.valueOf(i),"group-user-1", "demo-3", 100, true));
            t.start();
        }
    }

    private static void pollSyncCommit() {
        for(int i = 0; i < 4; i++) {
            //one consumer will be idle with 3 partitions
            Thread t = new Thread(new ConsumerThread(String.valueOf(i),"group-user-2", "demo-3", 100, false));
            t.start();
        }
    }

}
