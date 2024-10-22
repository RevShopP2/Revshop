package com.revshopp2.Cart.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.revshopp2.Cart.model.Cart;
import com.revshopp2.Cart.model.Product;
import com.revshopp2.Cart.service.CartService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private RestTemplate restTemplate;

	// Add product to cart
	// Helper method to get buyer ID from cookies
	private Long getBuyerIdFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("buyerId".equals(cookie.getName())) {
					try {
						return Long.parseLong(cookie.getValue());
					} catch (NumberFormatException e) {
						return null;
					}
				}
			}
		}
		return null;
	}

	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> toggleCart(@RequestParam("productId") Long productId,
			HttpServletRequest request) {
		// Retrieve buyer ID from cookies
		Map<String, Object> response = new HashMap<>();
		Long buyerId = getBuyerIdFromCookies(request); // This should fetch the buyer ID from cookies or session

		if (buyerId != null) {
			String productServiceUrl = "http://localhost:8080/products/cartController/" + productId; // Gateway URL to
																										// fetch product

			// Retrieve product using RestTemplate
			ResponseEntity<Product> response1 = restTemplate.getForEntity(productServiceUrl, Product.class);
			if (buyerId != null && response1.getBody() != null) {
				Product product = response1.getBody();
				if (product != null) {
					boolean isProductInCart = cartService.existsByBuyerAndProduct_ProductId(buyerId, productId);

					if (isProductInCart) {
						cartService.removeProductFromCart(buyerId, productId);
						response.put("message", "Product removed from cart.");
					} else {
						cartService.addProductToCart(buyerId, product);
						response.put("message", "Product added to cart.");
					}

					response.put("success", true);
				} else {
					response.put("success", false);
					response.put("errorMessage", "Product not found.");
				}
			}

		} else {
			response.put("success", false);
			response.put("errorMessage", "Please log in to manage your cart.");
		}

		return ResponseEntity.ok(response); // Return the JSON response
	}

	@GetMapping("/view")
	public String cartView(Model model, HttpServletRequest request) {
		Long buyerId = getBuyerIdFromCookies(request);
		if (buyerId != null) {
			List<Cart> cartItems = cartService.findAllByBuyer(buyerId);
			List<Product> productInCart = new ArrayList<>();

			for (Cart i : cartItems) {
				String productServiceUrl = "http://localhost:8080/products/cartController/" + i.getProductId();
				ResponseEntity<Product> response1 = restTemplate.getForEntity(productServiceUrl, Product.class);
				Product product = response1.getBody();
				productInCart.add(product);
			}
			model.addAttribute("cartItems", productInCart);
		}
		return "cart-view";
	}

	@PostMapping("/remove/{productId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable Long productId,
			HttpServletRequest request) {

		Long buyerId = getBuyerIdFromCookies(request);

		if (buyerId != null) {

			// Remove the product from the cart
			cartService.removeProductFromCart(buyerId, productId);
		}

		// Return success response as JSON
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/productController/{productId}")
	public ResponseEntity<Boolean> getProductById(@PathVariable("productId") Long productId,
			HttpServletRequest request) {
		Long buyerId = getBuyerIdFromCookies(request);
		Boolean apple = cartService.existsByBuyerAndProduct_ProductId(buyerId, productId);
		System.err.println(apple);
		return ResponseEntity.ok(apple);

	}

	@GetMapping("/allproductController/{buyerId}")
	public ResponseEntity<List<Cart>> getAllCartProducts(@PathVariable("buyerId") Long buyerId,
			HttpServletRequest request) {
		List<Cart> apple = cartService.findAllByBuyer(buyerId);
		System.err.println(apple);
		return ResponseEntity.ok(apple);

	}

	@GetMapping("/removefromcart")
	public ResponseEntity<Boolean> removeproductfromcart(@RequestParam("buyerId") Long buyerId,@RequestParam("productId") Long productId,
			HttpServletRequest request) {
		cartService.removeProductFromCart(buyerId,productId);
		Boolean apple=true;
		return ResponseEntity.ok(apple);

	}
	
	@PostMapping("/updateQuantity")
    public ResponseEntity<String> updateQuantity(@RequestParam Long productId, @RequestParam int quantity,HttpServletRequest request) {
        try {
        	Long buyerId=getBuyerIdFromCookies(request);
            cartService.updateProductQuantity(productId, buyerId,quantity);
            return ResponseEntity.ok("Quantity updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update quantity");
        }
    }
}
