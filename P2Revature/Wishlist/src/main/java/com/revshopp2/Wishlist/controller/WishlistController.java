
package com.revshopp2.Wishlist.controller;

import java.util.*;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.revshopp2.Wishlist.model.*;
import com.revshopp2.Wishlist.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/wish")
public class WishlistController {

	@Autowired
	private WishlistService wishService;

	@Autowired
	private RestTemplate restTemplate;

	// Add product to wishlist
	@PostMapping("/toggle")
	public ResponseEntity<Map<String, Object>> toggleWish(@RequestParam("productId") Long productId,
			HttpServletRequest request) {
		// Retrieve buyer ID from cookies
		Map<String, Object> response = new HashMap<>();
		Long buyerId = getBuyerIdFromCookies(request); // This should fetch the buyer ID from cookies or session
		if (buyerId != null) {
			if (productId != null) {
				boolean isProductInWish = wishService.existsByBuyerAndProduct_ProductId(buyerId, productId);
				if (isProductInWish) {
					System.out.println("Hi1");
					wishService.removeProductFromWishlist(buyerId, productId);
					response.put("message", "Product removed from Wish.");
				} else {
					System.out.println("Hi1");
					String productServiceUrl = "http://localhost:8080/products/cartController/" + productId;

					ResponseEntity<Product> response1 = restTemplate.getForEntity(productServiceUrl, Product.class);
					Product product =response1.getBody();
					wishService.addProductToWish(buyerId, product);
					response.put("message", "Product added to Wish.");
				}
				response.put("success", true);
			} else {
				response.put("success", false);
				response.put("errorMessage", "Product not found.");
			}

		} else {
			response.put("success", false);
			response.put("errorMessage", "Please log in to manage your cart.");
		}

		return ResponseEntity.ok(response);

	}

	// Method to remove a product from the wishlist
	@PostMapping("/remove/{productId}")
	public String removeFromWishlist(@PathVariable Long productId, HttpServletRequest request, Model model) {
		Long buyerId = null;

		// Retrieve buyer ID from cookies
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("buyerId")) {
					buyerId = Long.parseLong(cookie.getValue());
					break;
				}
			}
		}

		if (buyerId != null) {
			wishService.removeFromWishlist(buyerId, productId);
			List<Product> productInWish = (buyerId != null) ? wishService.findAllByBuyer(buyerId).stream()
					.map(i -> restTemplate
							.getForEntity("http://localhost:8080/products/cartController/" + productId, Product.class)
							.getBody())
					.collect(Collectors.toList()) : new ArrayList<>();

			model.addAttribute("wishlistItems", productInWish);

		}

		return "wishlist-view";
	}

	@GetMapping("/view")

	// Create a new Wishlist entry
	public String viewWishlist(Model model, HttpServletRequest request) {
		Long buyerId = getBuyerIdFromCookies(request);
		List<Product> productInWish = (buyerId != null) ? wishService.findAllByBuyer(buyerId).stream()
				.map(i -> restTemplate.getForEntity("http://localhost:8080/products/cartController/" + i.getProductId(),
						Product.class).getBody())
				.collect(Collectors.toList()) : new ArrayList<>();

		model.addAttribute("wishlistItems", productInWish);
		return "wishlist-view"; // Render the wishlist Thymeleaf view
	}

	private Long getBuyerIdFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("buyerId".equals(cookie.getName())) {
					return Long.parseLong(cookie.getValue());
				}
			}
		}
		return null;
	}

}
