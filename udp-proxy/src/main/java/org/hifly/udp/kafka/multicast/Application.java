package org.hifly.udp.kafka.multicast;

public class Application {

    private static int RECEIVERS = 5;
    private static String BIND_ADDRESS = "230.0.0.0";
    private static int BIND_PORT = 4446;
    private static String KAFKA_TOPIC = "telemetry";

    public static void main (String [] args) throws Exception {

        if(args !=null && args.length == 4) {
            RECEIVERS = args[0] != null && !args[0].isEmpty() ? Integer.parseInt(args[0]): RECEIVERS;
            BIND_ADDRESS = args[1] != null && !args[1].isEmpty() ? args[1]: BIND_ADDRESS;
            BIND_PORT = args[2] != null && !args[2].isEmpty() ? Integer.parseInt(args[2]): BIND_PORT;
            KAFKA_TOPIC = args[3] != null && !args[3].isEmpty() ? args[3]: KAFKA_TOPIC;
        }

        for (int i = 0; i < RECEIVERS; i++) {
            new MulticastReceiver(BIND_ADDRESS, BIND_PORT, KAFKA_TOPIC).start();
        }
    }

}