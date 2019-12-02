package com.redhat.kafka.demo.orders;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import org.apache.curator.test.TestingServer;

import java.io.IOException;
import java.util.Properties;


public class KafkaSuiteTest {

    private TestingServer zk;
    private KafkaServerStartable kafka;

    public void start() throws Exception {

        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181");
        props.put("broker.id", "1");
        props.put("offsets.topic.replication.factor", "1");
        props.put("transaction.state.log.replication.factor", "1");

        Integer port = getZkPort(props);
        zk = new TestingServer(port);
        zk.start();

        KafkaConfig kafkaConfig = new KafkaConfig(props);
        kafka = new KafkaServerStartable(kafkaConfig);
        kafka.startup();
    }

    public void start(Properties props) throws Exception {

        Integer port = getZkPort(props);
        zk = new TestingServer(port);
        zk.start();

        KafkaConfig kafkaConfig = new KafkaConfig(props);
        kafka = new KafkaServerStartable(kafkaConfig);
        kafka.startup();
    }

    public void stop() throws IOException {
        kafka.shutdown();
        zk.stop();
        zk.close();
    }

    private int getZkPort(Properties properties) {
        String url = (String) properties.get("zookeeper.connect");
        String port = url.split(":")[1];
        return Integer.valueOf(port);
    }

}