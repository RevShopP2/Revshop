package com.revshopp2.Order.repository;

import com.revshopp2.Order.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Check if a review exists by buyer and product
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Review r WHERE r.buyerId = :buyer AND r.productId = :product")
    boolean existsByBuyerAndProduct(@Param("buyer") Long buyerId, @Param("product") Long productId);

    // Retrieve all reviews for a product ordered by review ID in descending order
    @Query(value = "SELECT * FROM review WHERE product_id = ?1 ORDER BY review_id DESC", nativeQuery = true)
    List<Review> findByProductId(Long productId);

    // Retrieve all reviews for a specific buyer ordered by review ID in descending order
    @Query(value = "SELECT * FROM review WHERE buyer_id = ?1 ORDER BY review_id DESC", nativeQuery = true)
    List<Review> findByBuyerId(Long buyerId);
}
