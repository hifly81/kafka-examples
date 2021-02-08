package org.hifly.kafka.order.process.test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.hifly.kafka.order.process.event.OrderEvent;
import org.hifly.kafka.order.process.shipment.ShipmentClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ URL.class, ShipmentClient.class })
public class ShipmentClientTest {

    @Test
    public void test_nocontent() throws Exception {
        URL u = PowerMockito.mock(URL.class);
        String url = "http://1.2.3.4";
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);

        ShipmentClient shipmentClient = new ShipmentClient();
        Assert.assertEquals(204, shipmentClient.sendOrderEvent(url, new OrderEvent()));
    }

    @Test
    public void test_ok() throws Exception {
        URL urlObj = PowerMockito.mock(URL.class);
        String url = "http://1.2.3.4";
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(urlObj);
        HttpURLConnection urlConnection = PowerMockito.mock(HttpURLConnection.class);
        OutputStream outputStream = PowerMockito.mock(OutputStream.class);
        InputStream inputStream = PowerMockito.mock(InputStream.class);
        PowerMockito.when(urlObj.openConnection()).thenReturn(urlConnection);
        PowerMockito.when(urlConnection.getOutputStream()).thenReturn(outputStream);
        PowerMockito.when(urlConnection.getInputStream()).thenReturn(inputStream);
        PowerMockito.when(urlConnection.getResponseCode()).thenReturn(200);

        ShipmentClient shipmentClient = new ShipmentClient();
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setId("1");
        orderEvent.setName("Order1");
        orderEvent.setEventType(OrderEvent.EventType.ORDER_CREATED);
        orderEvent.setItemIds(Arrays.asList("1", "2"));
        Assert.assertEquals(200, shipmentClient.sendOrderEvent(url, orderEvent));
    }
}
