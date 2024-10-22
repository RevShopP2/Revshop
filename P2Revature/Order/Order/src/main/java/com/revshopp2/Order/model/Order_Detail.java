package com.revshopp2.Order.model;

import jakarta.persistence.*;

@Entity
@Table(name="order_detail")
public class Order_Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long order_detail_id; // Primary key

    @ManyToOne(cascade = CascadeType.ALL) // Specify the relationship
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order; // Foreign key referencing Orders

    private Long productId; // Foreign key referencing Product
    
   

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_per_unit", nullable = false)
    private double price_per_unit;

    private Long sellerId; // Foreign key referencing Seller

    @Column(name = "status", length = 50)
    private String status; // Order status

    // Default constructor
    public Order_Detail() {}

    // Parameterized constructor
    public Order_Detail(Orders order, Long productId, int quantity, double price_per_unit, Long sellerId, String status) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price_per_unit = price_per_unit;
        this.sellerId = sellerId;
        this.status = status;
     
    }
 

 
	// Getters and Setters
    public Long getOrder_detail_id() {
        return order_detail_id;
    }

    public void setOrder_detail_id(Long order_detail_id) {
        this.order_detail_id = order_detail_id;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    

    public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice_per_unit() {
        return price_per_unit;
    }

    public void setPrice_per_unit(double price_per_unit) {
        this.price_per_unit = price_per_unit;
    }

    public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}