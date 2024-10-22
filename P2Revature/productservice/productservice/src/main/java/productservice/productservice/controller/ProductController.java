package productservice.productservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.*;
import productservice.productservice.model.Category;
import productservice.productservice.model.Product;
import productservice.productservice.model.ReviewProducts;
import productservice.productservice.service.CategoryService;
import productservice.productservice.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CategoryService categoryService;

//    private SellerService sellerService;
	// Home or dashboard
	@GetMapping("/dashboard") // Change this if needed
	public String fromdashboard() {
		return "Buyerdashboard"; // Return the name of the detail view
	}

	@GetMapping("/welcome") // Change this if needed
	public String welcome() {
		return "welcomepage"; // Return the name of the detail view
	}

	@GetMapping("/seller/{sellerId}")
	public List<Product> getProductsBySellerId(@PathVariable Long sellerId) {
		return productService.getProductsBySellerId(sellerId);
	}

	@GetMapping("/manage")
	public String manageProducts(@RequestParam("sellerId") Long sellerId, Model model) {
		// Fetch products for the given sellerId
		List<Product> products = productService.getProductsBySellerId(sellerId);

		// Add products to the model
		model.addAttribute("products", products);
		model.addAttribute("sellerId", sellerId);
		// Return the manageProducts view
		return "manageProducts";
	}

	// When a particular item is selected from search results
	@GetMapping("/{id}")
	public String showProduct(@PathVariable Long id, Model model) {
		Product product = productService.getProductById(id);
		model.addAttribute("product", product);

		List<Product> availableProducts = productService.getAllProducts();
		model.addAttribute("availableProducts", availableProducts);
		
		String reviewUrl="http://localhost:8080/order/reviewController" + "?id=" + id;
		ResponseEntity<ReviewProducts> rp=restTemplate.getForEntity(reviewUrl, ReviewProducts.class);
		ReviewProducts reviewProductsObj=rp.getBody();
		model.addAttribute("comments", reviewProductsObj.getReviews());
		model.addAttribute("averageRating", reviewProductsObj.getAverageRating());
		model.addAttribute("reviewCount", reviewProductsObj.getReviewCount());
		model.addAttribute("starCounts",reviewProductsObj.getStarCounts());
		return "products"; // Return the name of the detail view
	}

	@GetMapping("/logout/category/{categoryId}")
	public String showProductswithoutlogoutByCategory(@PathVariable Long categoryId, Model model) {
		Category category = categoryService.getCategoryById(categoryId);
		List<Product> products = productService.getProductsByCategoryId(category.getCategoryId());
		model.addAttribute("products", products);
		return "logoutBuyerdashboardExtend"; // HTML page to display the products
	}

	@GetMapping("/logout/{id}")
	public String showlogoutProduct(@PathVariable Long id, Model model) {
		Product product = productService.getProductById(id);
		model.addAttribute("product", product);

		List<Product> availableProducts = productService.getAllProducts();
		model.addAttribute("availableProducts", availableProducts);
		String reviewUrl="http://localhost:8080/order/reviewController" + "?id=" + id;
		ResponseEntity<ReviewProducts> rp=restTemplate.getForEntity(reviewUrl, ReviewProducts.class);
		ReviewProducts reviewProductsObj=rp.getBody();
		model.addAttribute("comments", reviewProductsObj.getReviews());
		model.addAttribute("averageRating", reviewProductsObj.getAverageRating());
		model.addAttribute("reviewCount", reviewProductsObj.getReviewCount());
		model.addAttribute("starCounts",reviewProductsObj.getStarCounts());
		return "logoutproducts"; // Return the name of the detail view
	}

	@PostMapping("/logout/search")
	public String searchlogoutProducts(@RequestParam("keyword") String keyword, Model model) {
		List<Product> products;

		if (keyword == null || keyword.isEmpty()) {
			// No keyword provided, show all products
			products = productService.getAllProducts();
		} else {
			// Search by keyword (product name or category name)
			products = productService.searchProducts(keyword);
			if (products == null || products.isEmpty()) {
				// If no matching products are found, show all products
				products = productService.getAllProducts();
			}
		}

		model.addAttribute("products", products);
		model.addAttribute("keyword", keyword); // Retain the search keyword

		// You can decide where to redirect based on which page the search is performed
		// on
		return "logoutBuyerdashboardExtend"; // Render the results on a common search results page
	}

	@PostMapping("/search/buyerdashboard")
	public String searchProducts(@RequestParam("keyword") String keyword, Model model, HttpServletRequest request) {
		List<Product> products;
		Long buyerId = getBuyerIdFromCookies(request);
		if (keyword == null || keyword.isEmpty()) {
			// No keyword provided, show all products
			products = productService.getAllProducts();
		} else {
			// Search by keyword (product name or category name)
			products = productService.searchProducts(keyword);
			if (products == null || products.isEmpty()) {
				// If no matching products are found, show all products
				products = productService.getAllProducts();
			}
		}
		List<Long> cartProductIds = new ArrayList<>();
		if (buyerId != null) {

			for (Product product : products) {
				try {
					String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
					ResponseEntity<Boolean> response = restTemplate.getForEntity(productServiceUrl, Boolean.class);

					Boolean isInCart = response.getBody();
					System.out.println(isInCart);
					if (Boolean.TRUE.equals(isInCart)) {
						cartProductIds.add(product.getProductId());
					}
				} catch (Exception e) {
					System.err.println("Unexpected error: " + e.getMessage());
				}
			}
		}
		List<Long> wishllistProductIds = new ArrayList<>();
		model.addAttribute("products", products);

		model.addAttribute("keyword", keyword); // Retain the search keyword

		model.addAttribute("cartItems",
				cartProductIds != null && !cartProductIds.isEmpty() ? cartProductIds : new ArrayList<>());
		model.addAttribute("wishlistItems",
				wishllistProductIds != null && !wishllistProductIds.isEmpty() ? wishllistProductIds
						: new ArrayList<>());
		// You can decide where to redirect based on which page the search is performed
		// on
		return "BuyerdashboardExtend"; // Render the results on a common search results page
	}
	@GetMapping("/filter")
    public String searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "minPrice", defaultValue = "0") double minPrice,
            @RequestParam(value = "maxPrice", defaultValue = "100000") double maxPrice,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            @RequestParam(value = "source", defaultValue = "loggedOut") String source,
            Model model,HttpServletRequest request) {
		Long buyerId = getBuyerIdFromCookies(request);
        // Fetch filtered products based on the criteria
        List<Product> products = productService.searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder);

        // Add attributes to the model
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortOrder", sortOrder);
        List<Long> cartProductIds = new ArrayList<>();
		if (buyerId != null) {

			for (Product product : products) {
				try {
					String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
					ResponseEntity<Boolean> response = restTemplate.getForEntity(productServiceUrl, Boolean.class);

					Boolean isInCart = response.getBody();
					System.out.println(isInCart);
					if (Boolean.TRUE.equals(isInCart)) {
						cartProductIds.add(product.getProductId());
					}
				} catch (Exception e) {
					System.err.println("Unexpected error: " + e.getMessage());
				}
			}
		}

		List<Long> wishllistProductIds = new ArrayList<>();
		model.addAttribute("cartItems",
				cartProductIds != null && !cartProductIds.isEmpty() ? cartProductIds : new ArrayList<>());
		model.addAttribute("wishlistItems",
				wishllistProductIds != null && !wishllistProductIds.isEmpty() ? wishllistProductIds
						: new ArrayList<>());
     // Stay on the appropriate page based on the 'source' parameter
        return source.equals("loggedIn") ? "BuyerdashboardExtend" : "logoutBuyerDashboardExtend";
    }

	// Displaying the products as per category id from buyer dashboard
	@GetMapping("/category/{categoryId}")
	public String showProductsByCategory(@PathVariable Long categoryId, Model model,HttpServletRequest request) {
		Long buyerId = getBuyerIdFromCookies(request);
		List<Product> products = productService.getProductsByCategoryId(categoryId);
		model.addAttribute("products", products);
		List<Long> cartProductIds = new ArrayList<>();

		List<Long> wishllistProductIds = new ArrayList<>();
		if (buyerId != null) {

			for (Product product : products) {
				try {
					String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
					ResponseEntity<Boolean> response = restTemplate.getForEntity(productServiceUrl, Boolean.class);

					Boolean isInCart = response.getBody();
					System.out.println(isInCart);
					if (Boolean.TRUE.equals(isInCart)) {
						cartProductIds.add(product.getProductId());
					}
				} catch (Exception e) {
					System.err.println("Unexpected error: " + e.getMessage());
				}
			}
		}
		model.addAttribute("products", products);

		model.addAttribute("keyword", ""); // Retain the search keyword

		model.addAttribute("cartItems",
				cartProductIds != null && !cartProductIds.isEmpty() ? cartProductIds : new ArrayList<>());
		model.addAttribute("wishlistItems",
				wishllistProductIds != null && !wishllistProductIds.isEmpty() ? wishllistProductIds
						: new ArrayList<>());
		return "BuyerdashboardExtend";
		// HTML page to display the products
	}

	// Seller check
	@GetMapping("/dashboard/seller") // Optional: Change as necessary
	public String showDashboard(Model model) {
		return "SellerDashboard"; // Return the seller dashboard view
	}

	// Show the add product form based on category ID
	@GetMapping("/add")
	public String showAddProductForm(HttpServletRequest request, @RequestParam("categoryId") Long categoryId,
			Model model) {
		Category category = categoryService.getCategoryById(categoryId); // Fetch the category by ID
		model.addAttribute("category", category);
		String sellerId = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("sellerId")) {
					sellerId = cookie.getValue();
					break;
				}
			}
		}
		if (sellerId == null) {
			return "redirect:/ecom/LoginPage";
		}
		model.addAttribute("seller", sellerId);
		model.addAttribute("product", new Product()); // Add an empty product object for the form
		return "selleraddpro"; // Return the add product form view
	}

	// Save product in the selected category
	@PostMapping("/save")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("categoryId") Long categoryId,
			@RequestParam("sellerId") Long sellerId, HttpServletRequest request) {
		// Fetch the category by ID and set it to the product
		Category category = categoryService.getCategoryById(categoryId);
		product.setSellerId(sellerId);
		product.setCategory(category);

//
//        // Save the product
		productService.save(product);
		String gatewayUrl = ServletUriComponentsBuilder.fromCurrentContextPath().scheme(request.getScheme())
				.host(request.getServerName()).port(request.getServerPort()).path("/seller/SellerDashboard")
				.toUriString();
 

		// Redirect to the seller dashboard
		return "redirect:http://localhost:8080/seller/SellerDashboard";
	}

	@GetMapping("/cancel/{sellerId}")
	public String cancelPage(@PathVariable String sellerId, Model model) {

		List<Product> products = productService.getProductsBySellerId(Long.parseLong(sellerId));

		// Add products to the model
		model.addAttribute("products", products);
		model.addAttribute("sellerId", Long.parseLong(sellerId));
		// Return the manageProducts view
		return "manageProducts";
	}

	@DeleteMapping("/delete/{productId}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
		try {
			productService.deleteProductById(productId); // Assuming this service method deletes the product
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product.");
		}
	}

	@GetMapping("/edit/{productId}")
	public String showUpdateForm(@PathVariable("productId") Long productId, @RequestParam("sellerId") Long sellerId,
			Model model) {
		Product product = productService.getProductById(productId);
		model.addAttribute("product", product);
		model.addAttribute("sellerId", sellerId); // Add sellerId to the model
		return "updateProduct"; // This will load the updateProduct.html page
	}

	@PostMapping("/update/{productId}")
	public String updateProduct(@PathVariable("productId") Long productId,
			@ModelAttribute("product") Product updatedProduct, @RequestParam("sellerId") Long sellerId,
			HttpServletRequest request, Model model) {

		// Update the product
		productService.updateProduct(productId, updatedProduct);
		List<Product> products = productService.getProductsBySellerId(sellerId);

		// Add products to the model
		model.addAttribute("products", products);
		model.addAttribute("sellerId", sellerId);
		// Return the manageProducts view
		return "manageProducts";
	}

	public Long getBuyerIdFromCookies(HttpServletRequest request) {
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

	@GetMapping("/cartController/{productId}")
	public ResponseEntity<Product> getProductById(@PathVariable("productId") Long productId) {
		Product product = productService.getProductById(productId);
		
		if (product != null) {
			
			return ResponseEntity.ok(product);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@GetMapping("/updateProduct")
    public ResponseEntity<Boolean> updateProduct(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity) {

        Product product =productService.findById(productId);
        product.setQuantity(quantity);
        productService.save(product);

        return ResponseEntity.ok(true);
	}
}