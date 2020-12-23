package com.redhat.kafka.order.process;

import com.redhat.kafka.order.process.controller.OrderProcessController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "OrderApp", urlPatterns = "/order")
public class OrderProcessApp extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OrderProcessController orderProcessController = new OrderProcessController();
        orderProcessController.receiveOrders(3, "group-1", -1, 10);
    }

}
