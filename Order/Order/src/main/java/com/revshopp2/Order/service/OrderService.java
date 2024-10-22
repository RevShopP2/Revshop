package com.revshopp2.Order.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revshopp2.Order.model.Buyer;
import com.revshopp2.Order.model.Orders;
import com.revshopp2.Order.repository.OrderRepository;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Method to save an order
    public void saveOrder(Orders order) {
        orderRepository.save(order);
    }
    public List<Orders> getOrdersByBuyerId(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }
    
    public Orders createOrder(Buyer customer, double totalValue, String deliveryAddress) {
        Orders order = new Orders();
        order.setBuyerId(customer.getBuyerId());
        order.setTotalPrice(totalValue);
        order.setShippingAddress(deliveryAddress);
        order.setOrderDate(LocalDate.now());


        return orderRepository.save(order);
    }
}
