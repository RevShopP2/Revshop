package com.revshopp2.Wishlist.service;
 
import com.revshopp2.Wishlist.model.*;
import com.revshopp2.Wishlist.repository.WishlistRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepo;
    

    // Add product to wishlist
    public Wishlist addToWishlist(Wishlist wishlist) {
        return wishlistRepo.save(wishlist);
    }
    // Check if a product is in the buyer's wishlist
    public Wishlist getWishlistItem(Long buyerId, Long productId) {
        return wishlistRepo.findByBuyerIdAndProductId(buyerId, productId);
    }   
 
    public void removeFromWishlist(Long buyerId, Long productId) {
        Wishlist wishlistItem = wishlistRepo.findByBuyerIdAndProductId(buyerId, productId);
        if (wishlistItem != null) {
        	wishlistRepo.delete(wishlistItem);
        }
    }
    public boolean existsByBuyerAndProduct_ProductId(Long buyer, Long productId) {
		// TODO Auto-generated method stub
    	int c=wishlistRepo.existsByBuyerIdAndProductId(buyer,productId);
		return c>0;
	}
	@Transactional
	public void addProductToWish(Long buyerId,Product product) {
			Wishlist wishlist=new Wishlist();
			wishlist.setBuyerId(buyerId);
			wishlist.setProductId(product.getProductId());
			wishlist.setSellerId(product.getSellerId());
			wishlistRepo.save(wishlist);
	}
	@Transactional
	public void removeProductFromWishlist(Long buyerId,Long product) {
		wishlistRepo.deleteByBuyerIdAndProductId(buyerId, product);
	}
	public List<Wishlist> findAllByBuyer(Long buyerId){
		return wishlistRepo.findAllByBuyerId(buyerId);
	}
}


	 

