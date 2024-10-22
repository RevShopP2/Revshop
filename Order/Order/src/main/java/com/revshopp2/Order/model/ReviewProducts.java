package com.revshopp2.Order.model;

import java.util.*;

public class ReviewProducts {
    private List<ReviewForBuyer> reviews;
    private double averageRating;
    private int reviewCount;
    private int[] starCounts;
    private String name;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Constructors
    public ReviewProducts(List<ReviewForBuyer> reviews, double averageRating, int reviewCount, int[] starCounts) {
        this.reviews = reviews;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.starCounts = starCounts;
    }

    // Getters and Setters
    public List<ReviewForBuyer> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewForBuyer> reviews) {
        this.reviews = reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int[] getStarCounts() {
        return starCounts;
    }

    public void setStarCounts(int[] starCounts) {
        this.starCounts = starCounts;
    }
}

