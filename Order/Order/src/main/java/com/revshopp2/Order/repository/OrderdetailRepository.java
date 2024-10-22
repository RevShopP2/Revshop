package com.revshopp2.Order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.revshopp2.Order.model.Order_Detail;

@Repository
public interface OrderdetailRepository extends JpaRepository<Order_Detail, Long> {

    // Find all order details for a specific buyerId (via Orders entity)
    @Query("SELECT od FROM Order_Detail od WHERE od.order.buyerId = :buyerId ORDER BY od.order_detail_id DESC")
    List<Order_Detail> findByBuyerIdOrderByOrderDetailIdDesc(Long buyerId);

    // Find all order details for a specific sellerId
    @Query("SELECT od FROM Order_Detail od WHERE od.sellerId = :sellerId ORDER BY od.order_detail_id DESC")
    List<Order_Detail> findBySellerIdOrderByOrderDetailIdDesc(Long sellerId);

    // Find all order details by orderId
    List<Order_Detail> findByOrder_OrderId(Long orderId);
}
