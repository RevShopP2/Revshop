package com.revshopp2.Order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revshopp2.Order.model.Order_Detail;
import com.revshopp2.Order.repository.OrderdetailRepository;
 

@Service
public class OrderdetailService {
	
	@Autowired
	private OrderdetailRepository orderDetailRepository;
	
	public void addOrderDetails(Order_Detail orderDetails) {
		orderDetailRepository.save(orderDetails);
		
    }

	public List<Order_Detail> getOrdersByCustomerId(Long customerId) {
        return orderDetailRepository.findByBuyerIdOrderByOrderDetailIdDesc(customerId);
    }
	
	public List<Order_Detail> getOrderDetailByOrderId(Long orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId); // Fetch details by orderId
    }
	
	
	public List<Order_Detail> getOrdersBySellerId(Long sellerId) {
		return orderDetailRepository.findBySellerIdOrderByOrderDetailIdDesc(sellerId);
    }

    public void updateOrderStatus(Long orderId, String status) {
    	Order_Detail orderDetail = orderDetailRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            orderDetail.setStatus(status);
            orderDetailRepository.save(orderDetail);
    }



}
