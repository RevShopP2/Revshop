package com.revshopp2.Wishlist.repository;

import com.revshopp2.Wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Find a specific wishlist entry by buyerId and productId
    @Query("SELECT w FROM Wishlist w WHERE w.buyerId = :buyerId AND w.productId = :productId")
    Wishlist findByBuyerIdAndProductId(@Param("buyerId") Long buyerId, @Param("productId") Long productId);

    // Check if a product exists in the wishlist for a buyer
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.buyerId = :buyerId AND w.productId = :productId")
    int existsByBuyerIdAndProductId(@Param("buyerId") Long buyerId, @Param("productId") Long productId);

    // Delete a specific product from a buyer's wishlist
    @Modifying
    @Transactional
    @Query("DELETE FROM Wishlist w WHERE w.buyerId = :buyerId AND w.productId = :productId")
    void deleteByBuyerIdAndProductId(@Param("buyerId") Long buyerId, @Param("productId") Long productId);

    // Find all wishlist items for a specific buyer
    @Query("SELECT w FROM Wishlist w WHERE w.buyerId = :buyerId")
    List<Wishlist> findAllByBuyerId(@Param("buyerId") Long buyerId);
}
