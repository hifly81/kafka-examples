package org.hifly.kafka.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;

public class AdminClientWrapper {

    public static void main(String args []) {

        String configFile = "admin.properties";
        if(args.length == 1)
            configFile = args[0];

        String[] options = {
                "1 - Nodes listing",
                "2 - Topics listing",
                "3 - Fenced Brokers",
                "4 - Exit",
        };
        Scanner scanner = new Scanner(System.in);
        int option;
        while (true) {
            mainMenu(options);
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    nodesListing(configFile);
                    break;
                case 2:
                    topicsListing(configFile);
                    break;
                case 3:
                    fencedBrokers(configFile);
                    break;
                case 4:
                    System.exit(0);
                default:
                    System.out.println("Invalid operation!");
                    break;
            }
        }


    }

    private static void nodesListing(String configFile) {
        try (AdminClient client = AdminClient.create(loadConfig(configFile))) {
            DescribeClusterResult describeClusterResult = client.describeCluster();
            KafkaFuture<Collection<Node>> nodesFuture = describeClusterResult.nodes();
            Collection<Node> nodes = nodesFuture.get();
            for (Node node : nodes) {
                System.out.printf("Host:%s-Port:%s-Rack:%s\n", node.host(), node.port(), node.rack());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fencedBrokers(String configFile) {
        try (AdminClient client = AdminClient.create(loadConfig(configFile))) {
            DescribeClusterOptions options = new DescribeClusterOptions().includeFencedBrokers(true);
            DescribeClusterResult clusterResult = client.describeCluster(options);
            for (Node broker : clusterResult.nodes().get()) {
                boolean isFenced = broker.isFenced();
                System.out.printf("Broker: %s, Host: %s, Port: %d, Fenced: %s%n",
                        broker.id(), broker.host(), broker.port(), isFenced);
                //if(isFenced) {
                    //this removes from cluster metadata
                    //client.unregisterBroker(broker.id()).all().get();
                //}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void topicsListing(String configFile) {
        try (AdminClient client = AdminClient.create(loadConfig(configFile))) {
            ListTopicsResult listTopicsResult = client.listTopics();
            KafkaFuture<Collection<TopicListing>> listings = listTopicsResult.listings();
            Collection<TopicListing> topicListings = listings.get();
            for (TopicListing topicListing : topicListings) {
                System.out.printf("Name:%s-Internal?%s\n", topicListing.name(), topicListing.isInternal() ? "true" : "false");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mainMenu(String[] options){
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose operation: ");
    }


    private static Properties loadConfig(final String configFile) throws IOException {
        if (Files.exists(Paths.get(configFile))) {
            final Properties cfg = new Properties();
            try (InputStream inputStream = new FileInputStream(configFile)) {
                cfg.load(inputStream);
            }
            return cfg;
        } else {
            final Properties cfg = new Properties();
            try (InputStream is = AdminClientWrapper.class.getClassLoader().getResourceAsStream(configFile)) {
                cfg.load(is);
            }
            return cfg;
        }

    }
}
