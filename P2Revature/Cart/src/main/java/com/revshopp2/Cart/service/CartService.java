package com.revshopp2.Cart.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revshopp2.Cart.model.Cart;
import com.revshopp2.Cart.model.Product;
import com.revshopp2.Cart.repository.CartRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	// Add product to cart
	public Cart addToCart(Cart cart) {
		return cartRepository.save(cart);
	}

	// Get cart items for a specific buyer
	public List<Cart> getCartByBuyer(Long buyerId) {
		return cartRepository.findByBuyer_BuyerId(buyerId);
	}

	// Check if a product is in the buyer's cart
	public Cart getCartItem(Long buyerId, Long productId) {
		return cartRepository.findByBuyer_BuyerIdAndProductId(buyerId, productId);
	}
	////
//    public boolean isProductInCart(Long buyerId, Long productId) {
//        Cart cartItem = cartRepository.findByBuyerIdAndProductId(buyerId, productId);
//        return cartItem != null;
//    }

	@Transactional
	// Add product to cart
	public void addProductToCart(Long buyerId, Product product) {
		// Add new cart entry if the product is not in the cart
		Cart cart = new Cart();
		cart.setSellerId(product.getSellerId());
		cart.setBuyerId(buyerId);
		cart.setProductId(product.getProductId());
		cart.setQuantity(1); // Assuming starting quantity is 1
		cart.setPrice(product.getPrice()); // Set the price
		cartRepository.save(cart);

	}

	@Transactional
	// Remove product from cart
	public void removeProductFromCart(Long buyerId, Long productId) {
		System.out.println(buyerId + "Servicecart");
		cartRepository.deleteByBuyerAndProduct_ProductId(buyerId, productId);
	}

	public List<Cart> findAllByBuyer(Long buyer) {
		// TODO Auto-generated method stub
		return cartRepository.findAllByBuyer(buyer);
	}

	public boolean existsByBuyerAndProduct_ProductId(Long buyerId, Long productId) {
		// TODO Auto-generated method stub
		Long count = cartRepository.countByBuyerIdAndProductId(buyerId, productId);
		return count > 0;
	}

	public void clearCartForBuyer(Long buyerId) {
		List<Cart> cartItems = cartRepository.findAllByBuyer(buyerId);
		cartRepository.deleteAll(cartItems); // Deletes all items for the buyer
	}

	public void updateProductQuantity(Long productId, Long buyerId, int quantity) throws Exception {
		Cart cartItemOptional = cartRepository.findByBuyer_BuyerIdAndProductId(buyerId,productId);
		if (cartItemOptional!=null) {
			cartItemOptional.setQuantity(quantity);
			cartRepository.save(cartItemOptional);
		} else {
			throw new Exception("Product not found in cart");
		}
	}

}
