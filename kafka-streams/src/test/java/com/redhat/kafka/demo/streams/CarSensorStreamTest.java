package com.redhat.kafka.demo.streams;

import com.redhat.kafka.demo.streams.serializer.CarInfoSerializer;
import com.redhat.kafka.demo.streams.serializer.SpeedInfoDeserializer;
import com.redhat.kafka.demo.streams.stream.CarSensorStream;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import static org.apache.kafka.common.serialization.Serdes.String;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CarSensorStreamTest {

    private String TOPIC_CAR_SENSOR = "topic-car-sensor";
    private String TOPIC_CAR_INFO = "topic-car-info";
    private String TOPIC_OUT = "topic-out";

    @Test
    public void stream() throws Exception {
        Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamsProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "carsensor_app_id");
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        final Serializer<CarInfo> carInfoSerializer = new CarInfoSerializer();
        final Deserializer<SpeedInfo> speedInfoDeserializer = new SpeedInfoDeserializer();

        CarSensorStream carSensorStream = new CarSensorStream();

        Topology topology = carSensorStream.createTopology(TOPIC_CAR_SENSOR, TOPIC_CAR_INFO, TOPIC_OUT);

        try (TopologyTestDriver testDriver = new TopologyTestDriver(topology, streamsProps)) {

            testDriver.createInputTopic(TOPIC_CAR_SENSOR, String().serializer(), String().serializer())
                    .pipeKeyValueList(this.prepareInput());

            testDriver.createInputTopic(TOPIC_CAR_INFO, String().serializer(), carInfoSerializer)
                    .pipeKeyValueList(carInfoList());

            final List<SpeedInfo> speedInfo = testDriver
                    .createOutputTopic(TOPIC_OUT, String().deserializer(), speedInfoDeserializer).readValuesToList();
            
            System.out.println(speedInfo);

            assertThat(speedInfo, equalTo(expectedSpeedInfo()));

        }

    }

    private List<KeyValue<String, String>> prepareInput() {
        List<KeyValue<String, String>> sensors = Arrays.asList(
                new KeyValue<>("1", "{\"id\":\"1\",\"lat\":12.657,\"lng\":25.543,\"speed\":350}"),
                new KeyValue<>("2", "{\"id\":\"2\",\"lat\":16.657,\"lng\":23.543,\"speed\":360}"),
                new KeyValue<>("1", "{\"id\":\"1\",\"lat\":13.657,\"lng\":23.582,\"speed\":370}"),
                new KeyValue<>("3", "{\"id\":\"3\",\"lat\":13.657,\"lng\":23.582,\"speed\":120}"));
        return sensors;
    }

    private List<KeyValue<String, CarInfo>> carInfoList() {
        CarInfo carInfo = new CarInfo();
        carInfo.setId("1");
        carInfo.setBrand("Ferrari");
        carInfo.setModel("Testarossa");
        CarInfo carInfo2 = new CarInfo();
        carInfo2.setId("2");
        carInfo2.setBrand("Bugatti");
        carInfo2.setModel("Chiron");
        CarInfo carInfo3 = new CarInfo();
        carInfo3.setId("3");
        carInfo3.setBrand("Fiat");
        carInfo3.setModel("500");

        List<KeyValue<String, CarInfo>> cars = Arrays.asList(new KeyValue<>("1", carInfo),
                new KeyValue<>("2", carInfo2), new KeyValue<>("3", carInfo3));

        return cars;

    }

    private List<SpeedInfo> expectedSpeedInfo() {
        List<SpeedInfo> speed = new java.util.ArrayList<>();
        CarInfo carInfo = new CarInfo();
        carInfo.setId("1");
        carInfo.setBrand("Ferrari");
        carInfo.setModel("Testarossa");
        CarInfo carInfo2 = new CarInfo();
        carInfo2.setId("2");
        carInfo2.setBrand("Bugatti");
        carInfo2.setModel("Chiron");
        CarInfo carInfo3 = new CarInfo();
        carInfo3.setId("1");
        carInfo3.setBrand("Ferrari");
        carInfo3.setModel("Testarossa");

        SpeedInfo s1 = new SpeedInfo(350, carInfo);
        SpeedInfo s2 = new SpeedInfo(360, carInfo2);
        SpeedInfo s3 = new SpeedInfo(370, carInfo3);

        speed.add(s1);
        speed.add(s2);
        speed.add(s3);

        return speed;
    }

}
