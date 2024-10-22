package productservice.productservice;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import productservice.productservice.controller.ProductController;
import productservice.productservice.model.Category;
import productservice.productservice.model.Product;
import productservice.productservice.model.ReviewProducts;
import productservice.productservice.service.CategoryService;
import productservice.productservice.service.ProductService;

public class ProductControllerTest {
	@Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    private List<Product> products;
    private Product product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Sample product data
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        products = Arrays.asList(product1, product2);
    }
    
    @Test
    public void testFromDashboard() {
        String view = productController.fromdashboard();
        assertEquals("Buyerdashboard", view);
    }

    @Test
    public void testWelcome() {
        String view = productController.welcome();
        assertEquals("welcomepage", view);
    }

    @Test
    public void testGetProductsBySellerId() {
        Long sellerId = 1L;
        when(productService.getProductsBySellerId(sellerId)).thenReturn(products);

        List<Product> result = productController.getProductsBySellerId(sellerId);

        verify(productService).getProductsBySellerId(sellerId);
        assertEquals(products, result);
    }

    @Test
    public void testManageProducts() {
        Long sellerId = 1L;
        when(productService.getProductsBySellerId(sellerId)).thenReturn(products);

        String view = productController.manageProducts(sellerId, model);

        verify(productService).getProductsBySellerId(sellerId);
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("sellerId", sellerId);
        assertEquals("manageProducts", view);
    }
    
   
    @Test
    public void testShowProduct() {
        Long productId = 1L;

        // Mocking the productService to return a product
        when(productService.getProductById(productId)).thenReturn(products.get(0));
        when(productService.getAllProducts()).thenReturn(products);

        // Mocking the RestTemplate response
        ReviewProducts reviewProducts = new ReviewProducts(
                Arrays.asList(),  // Assuming empty reviews for the test
                4.5,              // Average rating
                10,               // Review count
                new int[]{5, 3, 2, 0, 0}  // Star counts
        );

        when(restTemplate.getForEntity("http://localhost:8080/order/reviewController?id=" + productId, ReviewProducts.class))
                .thenReturn(ResponseEntity.ok(reviewProducts));

        // Call the method
        String view = productController.showProduct(productId, model);

        // Verifying interactions and asserting results
        verify(productService).getProductById(productId);
        verify(productService).getAllProducts();
        verify(model).addAttribute("product", products.get(0));
        verify(model).addAttribute("availableProducts", products);
        verify(model).addAttribute("comments", reviewProducts.getReviews());
        verify(model).addAttribute("averageRating", reviewProducts.getAverageRating());
        verify(model).addAttribute("reviewCount", reviewProducts.getReviewCount());
        verify(model).addAttribute("starCounts", reviewProducts.getStarCounts());

        assertEquals("products", view);
    }
    
    
    @Test
    public void testShowProductswithoutlogoutByCategory() {
        // Arrange
        Long categoryId = 1L;

        // Mocking category service
        Category category = new Category();
        category.setCategoryId(categoryId);
        
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        // Mocking product service to return a list of products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");
        
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getProductsByCategoryId(categoryId)).thenReturn(products);

        // Act
        String view = productController.showProductswithoutlogoutByCategory(categoryId, model);

        // Assert
        verify(categoryService).getCategoryById(categoryId);
        verify(productService).getProductsByCategoryId(categoryId);
        verify(model).addAttribute("products", products);
        assertEquals("logoutBuyerdashboardExtend", view);
    }
    
    @Test
    public void testShowlogoutProduct() {
        // Arrange
        Long productId = 1L;

        // Mocking product service
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Product 1");

        when(productService.getProductById(productId)).thenReturn(product);

        // Mocking product service to return all products
        List<Product> availableProducts = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(availableProducts);

        // Mocking RestTemplate to simulate a review response
        ReviewProducts reviewProducts = new ReviewProducts(
                Arrays.asList(), // Assuming empty reviews for the test
                4.5,            // Average rating
                10,             // Review count
                new int[]{5, 3, 2, 0, 0} // Star counts
        );

        when(restTemplate.getForEntity("http://localhost:8080/order/reviewController?id=" + productId, ReviewProducts.class))
                .thenReturn(ResponseEntity.ok(reviewProducts));

        // Act
        String view = productController.showlogoutProduct(productId, model);

        // Assert
        verify(productService).getProductById(productId);
        verify(productService).getAllProducts();
        verify(model).addAttribute("product", product);
        verify(model).addAttribute("availableProducts", availableProducts);
        verify(model).addAttribute("comments", reviewProducts.getReviews());
        verify(model).addAttribute("averageRating", reviewProducts.getAverageRating());
        verify(model).addAttribute("reviewCount", reviewProducts.getReviewCount());
        verify(model).addAttribute("starCounts", reviewProducts.getStarCounts());
        assertEquals("logoutproducts", view);
    }
    
    @Test
    public void testSearchlogoutProducts_WithKeyword() {
        // Arrange
        String keyword = "Product 1";

        // Mocking product service to return a list of products matching the keyword
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.searchProducts(keyword)).thenReturn(products);

        // Act
        String view = productController.searchlogoutProducts(keyword, model);

        // Assert
        verify(productService).searchProducts(keyword);
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("keyword", keyword);
        assertEquals("logoutBuyerdashboardExtend", view);
    }

    @Test
    public void testSearchlogoutProducts_NoKeyword() {
        // Arrange
        String keyword = "";

        // Mocking product service to return a list of all products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> allProducts = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // Act
        String view = productController.searchlogoutProducts(keyword, model);

        // Assert
        verify(productService).getAllProducts();
        verify(model).addAttribute("products", allProducts);
        verify(model).addAttribute("keyword", keyword);
        assertEquals("logoutBuyerdashboardExtend", view);
    }

    @Test
    public void testSearchlogoutProducts_NoMatchingProducts() {
        // Arrange
        String keyword = "Nonexistent Product";

        // Mocking product service to return an empty list for search
        when(productService.searchProducts(keyword)).thenReturn(Arrays.asList());

        // Mocking product service to return all products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> allProducts = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // Act
        String view = productController.searchlogoutProducts(keyword, model);

        // Assert
        verify(productService).searchProducts(keyword);
        verify(productService).getAllProducts();
        verify(model).addAttribute("products", allProducts);
        verify(model).addAttribute("keyword", keyword);
        assertEquals("logoutBuyerdashboardExtend", view);
    }
    
    @Test
    public void testSearchProducts_WithKeyword() {
        // Arrange
        String keyword = "Product 1";
        Long buyerId = 1L;

        // Mocking product service to return a list of products matching the keyword
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.searchProducts(keyword)).thenReturn(products);
        
        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies); // Mock buyer ID

        // Mocking the response from the cart service
        for (Product product : products) {
            String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
            when(restTemplate.getForEntity(productServiceUrl, Boolean.class)).thenReturn(ResponseEntity.ok(true));
        }

        // Act
        String view = productController.searchProducts(keyword, model, request);

        // Assert
        verify(productService).searchProducts(keyword);
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("keyword", keyword);
        verify(model).addAttribute("cartItems", Arrays.asList(1L, 2L)); // Assuming both products are in cart
        verify(model).addAttribute("wishlistItems", new ArrayList<>());
        assertEquals("BuyerdashboardExtend", view);
    }

    @Test
    public void testSearchProducts_NoKeyword() {
        // Arrange
        String keyword = "";
        Long buyerId = 1L;

        // Mocking product service to return a list of all products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> allProducts = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(allProducts);
        
        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies);

        // Mocking the response from the cart service
        for (Product product : allProducts) {
            String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
            when(restTemplate.getForEntity(productServiceUrl, Boolean.class)).thenReturn(ResponseEntity.ok(false));
        }

        // Act
        String view = productController.searchProducts(keyword, model, request);

        // Assert
        verify(productService).getAllProducts();
        verify(model).addAttribute("products", allProducts);
        verify(model).addAttribute("keyword", keyword);
        verify(model).addAttribute("cartItems", new ArrayList<>()); // Assuming no products are in cart
        verify(model).addAttribute("wishlistItems", new ArrayList<>());
        assertEquals("BuyerdashboardExtend", view);
    }

    @Test
    public void testSearchProducts_NoMatchingProducts() {
        // Arrange
        String keyword = "Nonexistent Product";
        Long buyerId = 1L;

        // Mocking product service to return an empty list for search
        when(productService.searchProducts(keyword)).thenReturn(new ArrayList<>());
        
        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies);
 // Mock buyer ID

        // Mocking product service to return all products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> allProducts = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // Mocking the response from the cart service
        for (Product product : allProducts) {
            String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
            when(restTemplate.getForEntity(productServiceUrl, Boolean.class)).thenReturn(ResponseEntity.ok(false));
        }

        // Act
        String view = productController.searchProducts(keyword, model, request);

        // Assert
        verify(productService).searchProducts(keyword);
        verify(productService).getAllProducts();
        verify(model).addAttribute("products", allProducts);
        verify(model).addAttribute("keyword", keyword);
        verify(model).addAttribute("cartItems", new ArrayList<>()); // Assuming no products are in cart
        verify(model).addAttribute("wishlistItems", new ArrayList<>());
        assertEquals("BuyerdashboardExtend", view);
    } 
    
    @Test
    public void testSearchProducts_WithFilters_LoggedIn() {
        // Arrange
        String keyword = "Product";
        double minPrice = 10.0;
        double maxPrice = 100.0;
        String sortOrder = "asc";
        String source = "loggedIn";
        Long buyerId = 1L;

        // Mocking product service to return filtered products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder)).thenReturn(products);
        
        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies);

        // Mocking the response from the cart service
        for (Product product : products) {
            String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
            when(restTemplate.getForEntity(productServiceUrl, Boolean.class)).thenReturn(ResponseEntity.ok(true));
        }

        // Act
        String view = productController.searchProducts(keyword, minPrice, maxPrice, sortOrder, source, model, request);

        // Assert
        verify(productService).searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder);
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("keyword", keyword);
        verify(model).addAttribute("minPrice", minPrice);
        verify(model).addAttribute("maxPrice", maxPrice);
        verify(model).addAttribute("sortOrder", sortOrder);
        verify(model).addAttribute("cartItems", Arrays.asList(1L, 2L)); // Both products are in cart
        verify(model).addAttribute("wishlistItems", new ArrayList<>());
        assertEquals("BuyerdashboardExtend", view);
    }

    @Test
    public void testSearchProducts_WithFilters_LoggedOut() {
        // Arrange
        String keyword = "Product";
        double minPrice = 10.0;
        double maxPrice = 100.0;
        String sortOrder = "asc";
        String source = "loggedOut";

        // Mocking product service to return filtered products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder)).thenReturn(products);
        
        // Mocking the absence of buyer ID
        when(productController.getBuyerIdFromCookies(request)).thenReturn(null);

        // Act
        String view = productController.searchProducts(keyword, minPrice, maxPrice, sortOrder, source, model, request);

        // Assert
        verify(productService).searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder);
        verify(model).addAttribute("products", products);
        verify(model).addAttribute("keyword", keyword);
        verify(model).addAttribute("minPrice", minPrice);
        verify(model).addAttribute("maxPrice", maxPrice);
        verify(model).addAttribute("sortOrder", sortOrder);
        verify(model).addAttribute("cartItems", new ArrayList<>()); // No products in cart
        verify(model).addAttribute("wishlistItems", new ArrayList<>());
        assertEquals("logoutBuyerDashboardExtend", view);
    }

    @Test
    public void testSearchProducts_WithNoResults() {
        // Arrange
        String keyword = "Nonexistent Product";
        double minPrice = 10.0;
        double maxPrice = 100.0;
        String sortOrder = "asc";
        String source = "loggedIn";
        Long buyerId = 1L;

        // Mocking product service to return no products
        when(productService.searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder)).thenReturn(new ArrayList<>());

        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String view = productController.searchProducts(keyword, minPrice, maxPrice, sortOrder, source, model, request);

        // Assert
        verify(productService).searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder);
        verify(model).addAttribute("products", new ArrayList<>()); // No products found
        verify(model).addAttribute("keyword", keyword);
        verify(model).addAttribute("minPrice", minPrice);
        verify(model).addAttribute("maxPrice", maxPrice);
        verify(model).addAttribute("sortOrder", sortOrder);
        verify(model).addAttribute("cartItems", new ArrayList<>()); // No products in cart
        verify(model).addAttribute("wishlistItems", new ArrayList<>());
        assertEquals("BuyerdashboardExtend", view);
    }
    
    
    @Test
    public void testShowProductsByCategory_LoggedIn() {
        // Arrange
        Long categoryId = 1L;
        Long buyerId = 1L;

        // Mocking product service to return products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getProductsByCategoryId(categoryId)).thenReturn(products);
        
        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies);

        // Mocking the response from the cart service
        for (Product product : products) {
            String productServiceUrl = "http://localhost:8080/cart/productController/" + product.getProductId();
            when(restTemplate.getForEntity(productServiceUrl, Boolean.class)).thenReturn(ResponseEntity.ok(true));
        }

        // Act
        String view = productController.showProductsByCategory(categoryId, model, request);

        // Assert
        verify(productService).getProductsByCategoryId(categoryId);
        assertEquals("BuyerdashboardExtend", view);
    }

    @Test
    public void testShowProductsByCategory_LoggedOut() {
        // Arrange
        Long categoryId = 1L;

        // Mocking product service to return products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getProductsByCategoryId(categoryId)).thenReturn(products);
        
        // Mocking the absence of buyer ID
        when(productController.getBuyerIdFromCookies(request)).thenReturn(null);

        // Act
        String view = productController.showProductsByCategory(categoryId, model, request);

        // Assert
        verify(productService).getProductsByCategoryId(categoryId);

        assertEquals("BuyerdashboardExtend", view);
    }

    @Test
    public void testShowProductsByCategory_NoProducts() {
        // Arrange
        Long categoryId = 1L;
        Long buyerId = 1L;

        // Mocking product service to return no products
        when(productService.getProductsByCategoryId(categoryId)).thenReturn(new ArrayList<>());
        
        // Mocking cookies to return buyer ID
        Cookie[] cookies = new Cookie[]{ new Cookie("buyerId", buyerId.toString()) };
        when(request.getCookies()).thenReturn(cookies);


        // Act
        String view = productController.showProductsByCategory(categoryId, model, request);

        // Assert
        verify(productService).getProductsByCategoryId(categoryId);

        assertEquals("BuyerdashboardExtend", view);
    }
    
    @Test
    public void testShowAddProductForm_LoggedIn() {
        // Arrange
        Long categoryId = 1L;
        String sellerId = "123";
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");

        // Mocking the category service to return a category
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        
        // Mocking the cookies to include sellerId
        Cookie[] cookies = new Cookie[]{ new Cookie("sellerId", sellerId) };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String view = productController.showAddProductForm(request, categoryId, model);

        // Assert
        verify(categoryService).getCategoryById(categoryId);
        verify(model).addAttribute("category", category);
        verify(model).addAttribute("seller", sellerId);
 // Check that an empty product object is added
        assertEquals("selleraddpro", view); // Check that the correct view is returned
    }

    @Test
    public void testShowAddProductForm_NotLoggedIn() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");

        // Mocking the category service to return a category
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        
        // Mocking the absence of cookies
        when(request.getCookies()).thenReturn(null);

        // Act
        String view = productController.showAddProductForm(request, categoryId, model);

        // Assert
        verify(categoryService).getCategoryById(categoryId);
        // Check that a redirect occurs if there is no sellerId
        assertEquals("redirect:/ecom/LoginPage", view);
    }

    @Test
    public void testShowAddProductForm_NoSellerIdCookie() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Electronics");

        // Mocking the category service to return a category
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        
        // Mocking cookies that do not include sellerId
        Cookie[] cookies = new Cookie[]{ new Cookie("otherId", "456") };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String view = productController.showAddProductForm(request, categoryId, model);

        // Assert
        verify(categoryService).getCategoryById(categoryId);
        // Check that a redirect occurs if there is no sellerId
        assertEquals("redirect:/ecom/LoginPage", view);
    }
    
    @Test
    public void testShowDashboard() {
        // Act
        String view = productController.showDashboard(model);

        // Assert
        assertEquals("SellerDashboard", view); // Check that the correct view is returned
    }
    
    
    @Test
    public void testGetProductById_ProductExists() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Product");

        when(productService.getProductById(productId)).thenReturn(product);

        // Act
        ResponseEntity<Product> response = productController.getProductById(productId);

        // Assert
        assertEquals(200, response.getStatusCodeValue()); // HTTP 200 OK
        assertEquals(product, response.getBody()); // Product should match
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        // Arrange
        Long productId = 1L;

        when(productService.getProductById(productId)).thenReturn(null);

        // Act
        ResponseEntity<Product> response = productController.getProductById(productId);

        // Assert
        assertEquals(404, response.getStatusCodeValue()); // HTTP 404 Not Found
    }

    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        Long productId = 1L;
        int newQuantity = 10;
        Product product = new Product();
        product.setProductId(productId);
        product.setQuantity(5); // Initial quantity

        when(productService.findById(productId)).thenReturn(product);
        
        // Act
        ResponseEntity<Boolean> response = productController.updateProduct(productId, newQuantity);

        // Assert
        assertEquals(200, response.getStatusCodeValue()); // HTTP 200 OK
        assertEquals(true, response.getBody()); // Should return true
        assertEquals(newQuantity, product.getQuantity()); // Verify quantity is updated
    }
    
    
    @Test
    public void testShowUpdateForm() {
        // Arrange
        Long productId = 1L;
        Long sellerId = 2L;
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Product");

        when(productService.getProductById(productId)).thenReturn(product);

        // Act
        String viewName = productController.showUpdateForm(productId, sellerId, model);

        // Assert
        assertEquals("updateProduct", viewName); // Should return the updateProduct view
        verify(model).addAttribute("product", product); // Should add the product to the model
        verify(model).addAttribute("sellerId", sellerId); // Should add the sellerId to the model
    }

    @Test
    public void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
        Long sellerId = 2L;
        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setProductName("Updated Product");

        // Act
        String viewName = productController.updateProduct(productId, updatedProduct, sellerId, mock(HttpServletRequest.class), model);

        // Assert
        verify(productService).updateProduct(productId, updatedProduct); // Should call the updateProduct method
        verify(productService).getProductsBySellerId(sellerId); // Should call to get products by seller ID
 // Should add the sellerId to the model
        assertEquals("manageProducts", viewName); // Should return the manageProducts view
    }
    
    
    @Test
    public void testCancelPage() {
        // Arrange
        String sellerId = "1";
        Long sellerIdLong = Long.parseLong(sellerId);
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("Product 1");

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("Product 2");

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getProductsBySellerId(sellerIdLong)).thenReturn(products);

        // Act
        String viewName = productController.cancelPage(sellerId, model);

        // Assert
        assertEquals("manageProducts", viewName); // Should return the manageProducts view
        verify(model).addAttribute("products", products); // Should add products to the model
        verify(model).addAttribute("sellerId", sellerIdLong); // Should add sellerId to the model
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        Long productId = 1L;

        // Act
        ResponseEntity<?> responseEntity = productController.deleteProduct(productId);

        // Assert
        verify(productService).deleteProductById(productId); // Should call deleteProductById
        assertEquals(ResponseEntity.ok().build(), responseEntity); // Should return a 200 OK response
    }

    @Test
    public void testDeleteProduct_Failure() {
        // Arrange
        Long productId = 1L;
        doThrow(new RuntimeException("Failed to delete product.")).when(productService).deleteProductById(productId);

        // Act
        ResponseEntity<?> responseEntity = productController.deleteProduct(productId);

        // Assert
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product."), responseEntity);
    }
    


    
}

