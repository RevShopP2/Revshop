package com.revshopp2.Cart.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revshopp2.Cart.model.Cart;

import jakarta.transaction.Transactional;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Finds all cart items by buyer ID
    @Query(value = "SELECT * FROM cart WHERE buyer_id = :buyerId", nativeQuery = true)
    List<Cart> findByBuyer_BuyerId(Long buyerId);

    // Finds a specific product in the buyer's cart
    @Query(value = "SELECT * FROM cart WHERE buyer_id = :buyerId AND product_id = :productId", nativeQuery = true)
    Cart findByBuyer_BuyerIdAndProductId(Long buyerId, Long productId);

	
	
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.buyerId = :buyerId AND c.productId = :productId")
    Long countByBuyerIdAndProductId(@Param("buyerId") Long buyerId, @Param("productId") Long productId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart WHERE buyer_id = ?1 AND product_id = ?2", nativeQuery = true)
    void deleteByBuyerAndProduct_ProductId(Long buyerId, Long productId);

	@Query(value = "SELECT * FROM cart WHERE buyer_id = ?1", nativeQuery = true)
    List<Cart> findAllByBuyer(Long buyerId);
	

    
//    
//    @Query("SELECT c FROM CartItem c WHERE c.buyer.id = :buyerId AND c.product.id = :productId")
//    Cart findByBuyerIdAndProductId(@Param("buyerId") Long buyerId, @Param("productId") Long productId);

}
