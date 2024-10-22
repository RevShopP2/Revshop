package com.revshopp2.Order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revshopp2.Order.model.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    // Find a specific order by its order ID
    Orders findByOrderId(Long orderId);

    // Find all orders where buyerId matches the given value
    List<Orders> findByBuyerId(Long buyerId);
}
