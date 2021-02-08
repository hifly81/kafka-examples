package org.hifly.kafka.order;

import org.hifly.kafka.order.controller.OrderController;
import org.hifly.kafka.order.model.Order;
import org.hifly.kafka.order.model.OrderItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "OrderApp", urlPatterns = "/order")
public class OrderApp extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OrderController orderController = new OrderController();

        for (int i = 0; i < 500; i++) {
            Order order = new Order();
            String orderId = "ID-" + UUID.randomUUID().toString();
            order.setId(orderId);
            order.setName(UUID.randomUUID().toString());

            OrderItem orderItem1 = new OrderItem();
            orderItem1.setId(UUID.randomUUID().toString());
            orderItem1.setName(UUID.randomUUID().toString());
            orderItem1.setPrice(100);
            orderItem1.setOrder(order);

            OrderItem orderItem2 = new OrderItem();
            orderItem2.setId(UUID.randomUUID().toString());
            orderItem2.setName(UUID.randomUUID().toString());
            orderItem2.setPrice(320);
            orderItem2.setOrder(order);

            List<OrderItem> items = new ArrayList<>();
            items.add(orderItem1);
            items.add(orderItem2);
            order.setItems(items);

            orderController.create(order);

            for (OrderItem item : items)
                orderController.itemReady(item);

            orderController.ready(orderId);
        }


    }


}
