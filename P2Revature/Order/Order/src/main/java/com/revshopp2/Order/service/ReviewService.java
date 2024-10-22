package com.revshopp2.Order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revshopp2.Order.model.Buyer;
import com.revshopp2.Order.model.Product;
import com.revshopp2.Order.model.Review;
import com.revshopp2.Order.repository.ReviewRepository;



@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository; // Assuming you have a JPA repository

    public void saveReview(Review review) {
        reviewRepository.save(review); // Save the review to the database
    }
    public boolean submitReview(Product product, Buyer buyer, Integer rating, String comment) {
        Review review = new Review();
        review.setProductId(product.getProductId()); // Assuming Product has a getId() method
        review.setBuyerId(buyer.getBuyerId()); // Assuming you have a method to get the current order ID
        review.setRating(rating);
        review.setComment(comment);
        
        reviewRepository.save(review); // Save the review to the database
        return true; // Return true to indicate success
    }    
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    
    public boolean existsByCustomerAndProduct(Long buyer, Long product) {

        return reviewRepository.existsByBuyerAndProduct(buyer,product);
    }
    
    
}