package com.revshopp2.Order;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;

import com.revshopp2.Order.model.Buyer;
import com.revshopp2.Order.model.Product;
import com.revshopp2.Order.model.Review;
import com.revshopp2.Order.repository.ReviewRepository;
import com.revshopp2.Order.service.ReviewService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReview() {
        Review review = new Review();
        reviewService.saveReview(review);
        verify(reviewRepository).save(review);
    }

    @Test
    void testSubmitReview() {
        Product product = new Product();
        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        Integer rating = 5;
        String comment = "Great product!";

        boolean result = reviewService.submitReview(product, buyer, rating, comment);
        
        verify(reviewRepository).save(any(Review.class));
        assertTrue(result);
    }

    @Test
    void testGetReviewsByProductId() {
        Long productId = 1L;
        List<Review> reviewList = new ArrayList<>();
        when(reviewRepository.findByProductId(productId)).thenReturn(reviewList);
        
        List<Review> result = reviewService.getReviewsByProductId(productId);
        
        verify(reviewRepository).findByProductId(productId);
        assertSame(reviewList, result);
    }

    @Test
    void testExistsByCustomerAndProduct() {
        Long buyerId = 1L;
        Long productId = 1L;
        when(reviewRepository.existsByBuyerAndProduct(buyerId, productId)).thenReturn(true);
        
        boolean result = reviewService.existsByCustomerAndProduct(buyerId, productId);
        
        verify(reviewRepository).existsByBuyerAndProduct(buyerId, productId);
        assertTrue(result);
    }
}

