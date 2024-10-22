package com.revshopp2.Order.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "orders") // Specify the table name
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long orderId; // Primary key

	private Long buyerId; // Foreign key referencing Customer

	@Column(name = "total_amount", nullable = false)
	private double totalPrice;

	@Column(name = "delivery_address", nullable = false)
	private String shippingAddress;

	@Column(name = "order_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDate orderDate;

	// Default constructor
	public Orders() {
	}

	// Parameterized constructor
	public Orders(Long buyerId, double totalPrice, String shippingAddress) {
		this.buyerId = buyerId;
		this.totalPrice = totalPrice;
		this.shippingAddress = shippingAddress;
		this.orderDate = LocalDate.now(); // Set current timestamp
	}

	// Getters and Setters
	public Long getTransaction_id() {
		return orderId;
	}

	public void setTransaction_id(Long transaction_id) {
		this.orderId = transaction_id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

}