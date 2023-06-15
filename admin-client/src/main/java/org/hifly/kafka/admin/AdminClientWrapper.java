package org.hifly.kafka.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

public class AdminClientWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminClientWrapper.class);

    public static void main(String args []) {

        String configFile = "admin.properties";
        if(args.length == 1)
            configFile = args[0];

        String[] options = {
                "1 - Nodes listing",
                "2 - Topics listing",
                "3 - Exit",
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
            Iterator iterator = nodes.iterator();
            while ( iterator.hasNext()){
                Node node = (Node)iterator.next();
                LOGGER.info("Host:{}-Port:{}-Rack:{}\n", node.host(), node.port(), node.rack());
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
            Iterator iterator = topicListings.iterator();
            while ( iterator.hasNext()){
                TopicListing topicListing = (TopicListing)iterator.next();
                LOGGER.info("Name:{}-Internal?{}\n", topicListing.name(), topicListing.isInternal()?"true":"false");
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
