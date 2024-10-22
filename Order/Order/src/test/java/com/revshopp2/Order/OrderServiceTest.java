package com.revshopp2.Order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import com.revshopp2.Order.model.Buyer;
import com.revshopp2.Order.model.Orders;
import com.revshopp2.Order.repository.OrderRepository;
import com.revshopp2.Order.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveOrder() {
        Orders order = new Orders();
        orderService.saveOrder(order);
        verify(orderRepository).save(order);
    }

    @Test
    void testGetOrdersByBuyerId() {
        Long buyerId = 1L;
        List<Orders> ordersList = new ArrayList<>();
        when(orderRepository.findByBuyerId(buyerId)).thenReturn(ordersList);
        
        List<Orders> result = orderService.getOrdersByBuyerId(buyerId);
        
        verify(orderRepository).findByBuyerId(buyerId);
        assertSame(ordersList, result);
    }


    @Test
    void testCreateOrder() {
        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        double totalValue = 100.0;
        String deliveryAddress = "123 Street";

        Orders mockOrder = new Orders();
        mockOrder.setBuyerId(buyer.getBuyerId());
        mockOrder.setTotalPrice(totalValue);
        mockOrder.setShippingAddress(deliveryAddress);
        mockOrder.setOrderDate(LocalDate.now());

        // Mocking the orderRepository.save method to return the mockOrder
        when(orderRepository.save(any(Orders.class))).thenReturn(mockOrder);

        // Call the method under test
        Orders order = orderService.createOrder(buyer, totalValue, deliveryAddress);
        
        // Verify the interaction and assertions
        verify(orderRepository).save(any(Orders.class));
        assertNotNull(order);
        assertEquals(buyer.getBuyerId(), order.getBuyerId());
        assertEquals(totalValue, order.getTotalPrice());
    }

}

