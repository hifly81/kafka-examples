package org.hifly.kafka.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class AdminClientWrapper {

    public static void main(String args []) {

        String configFile = "admin.properties";
        if(args.length == 1)
            configFile = args[0];
        try (AdminClient client = AdminClient.create(loadConfig(configFile))) {
            DescribeClusterResult describeClusterResult = client.describeCluster();
            KafkaFuture<Collection<Node>> nodesFuture = describeClusterResult.nodes();
            Collection<Node> nodes = nodesFuture.get();
            Iterator iterator = nodes.iterator();
            while ( iterator.hasNext()){
                Node node = (Node)iterator.next();
                System.out.printf("Host:%s-Port:%s-Rack:%s\n", node.host(), node.port(), node.rack());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
