package com.revshopp2.Order;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import com.revshopp2.Order.model.Order_Detail;
import com.revshopp2.Order.repository.OrderdetailRepository;
import com.revshopp2.Order.service.OrderdetailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderdetailServiceTest {

    @Mock
    private OrderdetailRepository orderDetailRepository;

    @InjectMocks
    private OrderdetailService orderDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrderDetails() {
        Order_Detail orderDetail = new Order_Detail();
        orderDetailService.addOrderDetails(orderDetail);
        verify(orderDetailRepository).save(orderDetail);
    }

    @Test
    void testGetOrdersByCustomerId() {
        Long customerId = 1L;
        List<Order_Detail> orderDetailsList = new ArrayList<>();
        when(orderDetailRepository.findByBuyerIdOrderByOrderDetailIdDesc(customerId)).thenReturn(orderDetailsList);
        
        List<Order_Detail> result = orderDetailService.getOrdersByCustomerId(customerId);
        
        verify(orderDetailRepository).findByBuyerIdOrderByOrderDetailIdDesc(customerId);
        assertSame(orderDetailsList, result);
    }

    @Test
    void testGetOrderDetailByOrderId() {
        Long orderId = 1L;
        List<Order_Detail> orderDetailsList = new ArrayList<>();
        when(orderDetailRepository.findByOrder_OrderId(orderId)).thenReturn(orderDetailsList);
        
        List<Order_Detail> result = orderDetailService.getOrderDetailByOrderId(orderId);
        
        verify(orderDetailRepository).findByOrder_OrderId(orderId);
        assertSame(orderDetailsList, result);
    }

    @Test
    void testGetOrdersBySellerId() {
        Long sellerId = 1L;
        List<Order_Detail> orderDetailsList = new ArrayList<>();
        when(orderDetailRepository.findBySellerIdOrderByOrderDetailIdDesc(sellerId)).thenReturn(orderDetailsList);
        
        List<Order_Detail> result = orderDetailService.getOrdersBySellerId(sellerId);
        
        verify(orderDetailRepository).findBySellerIdOrderByOrderDetailIdDesc(sellerId);
        assertSame(orderDetailsList, result);
    }

    @Test
    void testUpdateOrderStatus() {
        Long orderId = 1L;
        String status = "Shipped";
        Order_Detail orderDetail = new Order_Detail();
        when(orderDetailRepository.findById(orderId)).thenReturn(Optional.of(orderDetail));

        orderDetailService.updateOrderStatus(orderId, status);
        
        verify(orderDetailRepository).save(orderDetail);
        assertEquals(status, orderDetail.getStatus());
    }
}

