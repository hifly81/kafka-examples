package org.hifly.kafka.demo.streams;

import org.hifly.kafka.demo.streams.domain.CarInfo;
import org.hifly.kafka.demo.streams.domain.CarSensor;
import org.hifly.kafka.demo.streams.domain.SpeedInfo;
import org.hifly.kafka.demo.streams.serializer.CarInfoSerializer;
import org.hifly.kafka.demo.streams.serializer.CarSensorSerializer;
import org.hifly.kafka.demo.streams.serializer.SpeedInfoDeserializer;
import org.hifly.kafka.demo.streams.stream.CarSensorStream;
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
    public void stream() {
        Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        final Deserializer<SpeedInfo> speedInfoDeserializer = new SpeedInfoDeserializer();
        final Serializer<CarSensor> carSensorSerializer = new CarSensorSerializer();

        CarSensorStream carSensorStream = new CarSensorStream();

        Topology topology = carSensorStream.createTopology(TOPIC_CAR_SENSOR, TOPIC_CAR_INFO, TOPIC_OUT);

        try (TopologyTestDriver testDriver = new TopologyTestDriver(topology, streamsProps)) {

            testDriver.createInputTopic(TOPIC_CAR_INFO, String().serializer(), new CarInfoSerializer())
                    .pipeKeyValueList(carInfoList());

            testDriver.createInputTopic(TOPIC_CAR_SENSOR, String().serializer(), carSensorSerializer)
                    .pipeKeyValueList(this.prepareInput());

            final List<SpeedInfo> speedInfo = testDriver
                    .createOutputTopic(TOPIC_OUT, String().deserializer(), speedInfoDeserializer).readValuesToList();

            assertThat(speedInfo, equalTo(expectedSpeedInfo()));

        }

    }

    private List<KeyValue<String, CarSensor>> prepareInput() {
        CarSensor carSensor1 = new CarSensor();
        carSensor1.setId("1");
        carSensor1.setSpeed(350);
        carSensor1.setLat(12.657f);
        carSensor1.setLng(25.543f);
        CarSensor carSensor2 = new CarSensor();
        carSensor2.setId("2");
        carSensor2.setSpeed(360);
        carSensor2.setLat(16.657f);
        carSensor2.setLng(23.543f);
        CarSensor carSensor3 = new CarSensor();
        carSensor3.setId("1");
        carSensor3.setSpeed(370);
        carSensor3.setLat(13.657f);
        carSensor3.setLng(23.582f);
        CarSensor carSensor4 = new CarSensor();
        carSensor4.setId("3");
        carSensor4.setSpeed(120);
        carSensor4.setLat(13.657f);
        carSensor4.setLng(23.582f);
        List<KeyValue<String, CarSensor>> sensors = Arrays.asList(
                new KeyValue<>("1", carSensor1),
                new KeyValue<>("2", carSensor2),
                new KeyValue<>("1", carSensor3),
                new KeyValue<>("3", carSensor4));
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

        List<KeyValue<String, CarInfo>> cars = Arrays.asList(
                new KeyValue<>("1", carInfo),
                new KeyValue<>("2", carInfo2),
                new KeyValue<>("3", carInfo3));
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
