package org.hifly.kafka.demo.streams;

import org.hifly.kafka.demo.streams.domain.CarInfo;
import org.hifly.kafka.demo.streams.serializer.CarInfoDeserializer;
import org.hifly.kafka.demo.streams.serializer.CarInfoSerializer;
import org.hifly.kafka.demo.streams.stream.CarBrandStream;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.apache.kafka.common.serialization.Serdes.String;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CarBrandStreamTest {

    private String TOPIC_CAR_INFO = "topic-car-info";
    private String TOPIC_FERRARI_OUTPUT = "topic-ferrari-output";
    private String TOPIC_CAR_OUTPUT = "topic-car-output";

    @Test
    public void stream() {
        Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        final Serializer<CarInfo> carInfoSerializer = new CarInfoSerializer();
        final Deserializer<CarInfo> carInfoDeserializer = new CarInfoDeserializer();

        CarBrandStream carBrandStream = new CarBrandStream();

        Topology topology = carBrandStream.createTopology(TOPIC_CAR_INFO, TOPIC_FERRARI_OUTPUT, TOPIC_CAR_OUTPUT);
        TopologyTestDriver testDriver = new TopologyTestDriver(topology, streamsProps);

        testDriver
            .createInputTopic(TOPIC_CAR_INFO, String().serializer(), carInfoSerializer)
            .pipeValueList(this.prepareInput());

        final List<CarInfo> ferrari =
            testDriver.createOutputTopic(TOPIC_FERRARI_OUTPUT, String().deserializer(), carInfoDeserializer)
                .readValuesToList();

        assertThat(ferrari, equalTo(expectedFerrari()));

        final List<CarInfo> cars =
            testDriver.createOutputTopic(TOPIC_CAR_OUTPUT, String().deserializer(), carInfoDeserializer)
                .readValuesToList();

        assertThat(cars, equalTo(expectedCars()));

        testDriver.close();
    }

    private List<CarInfo> prepareInput() {
        List<CarInfo> cars = new java.util.ArrayList<>();
        CarInfo carInfo = new CarInfo();
        carInfo.setId("1");
        carInfo.setBrand("Ferrari");
        carInfo.setModel("Testarossa");
        CarInfo carInfo2 = new CarInfo();
        carInfo2.setId("2");
        carInfo2.setBrand("Bugatti");
        carInfo2.setModel("Chiron");
        CarInfo carInfo3= new CarInfo();
        carInfo3.setId("3");
        carInfo3.setBrand("Ferrari");
        carInfo3.setModel("F40");
        cars.add(carInfo);
        cars.add(carInfo2);
        cars.add(carInfo3);
        return cars;
      }

      private List<CarInfo> expectedFerrari() {
        List<CarInfo> ferrari = new java.util.ArrayList<>();
        CarInfo carInfo = new CarInfo();
        carInfo.setId("1");
        carInfo.setBrand("Ferrari");
        carInfo.setModel("Testarossa");
        CarInfo carInfo2 = new CarInfo();
        carInfo2.setId("3");
        carInfo2.setBrand("Ferrari");
        carInfo2.setModel("F40");
        ferrari.add(carInfo);
        ferrari.add(carInfo2);
        return ferrari;
      }

      private List<CarInfo> expectedCars() {
        List<CarInfo> cars = new java.util.ArrayList<>();
        CarInfo carInfo = new CarInfo();
        carInfo.setId("2");
        carInfo.setBrand("Bugatti");
        carInfo.setModel("Chiron");
        cars.add(carInfo);
        return cars;
      }

}
