package com.revshopp2.Order;

import com.github.andrewoma.dexx.collection.HashMap;
import com.github.andrewoma.dexx.collection.Map;
import com.revshopp2.Order.controller.Ordercontroller;
import com.revshopp2.Order.model.Buyer;
import com.revshopp2.Order.model.Cart;
import com.revshopp2.Order.model.Order_Detail;
import com.revshopp2.Order.model.Orders;
import com.revshopp2.Order.model.Product;
import com.revshopp2.Order.model.ReceivedOrders;
import com.revshopp2.Order.model.Review;
import com.revshopp2.Order.model.ReviewProducts;
import com.revshopp2.Order.model.Seller;
import com.revshopp2.Order.service.EmailService;
import com.revshopp2.Order.service.OrderService;
import com.revshopp2.Order.service.OrderdetailService;
import com.revshopp2.Order.service.ReviewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class OrderControllerTest {

    @InjectMocks
    private Ordercontroller orderController;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderdetailService orderdetailService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private EmailService emailService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    
    
    @Test
    public void testConfirmCheckout_Success() throws Exception{
        // Setup mock data
        Long buyerId = 1L;
        String paymentMethod = "Credit Card";
        Buyer buyer = new Buyer();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");
        buyer.setStreet("123 Main St");
        buyer.setCity("Springfield");
        buyer.setState("IL");
        buyer.setEmail("john.doe@example.com");

        Cart cartItem = new Cart();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(100.0);
        product.setDiscountPrice(10.0);
        product.setQuantity(50);
        product.setImage("image.jpg");
        product.setProductName("Product 1");

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("buyerId", String.valueOf(buyerId))});
        when(restTemplate.getForEntity(any(String.class), eq(Buyer.class))).thenReturn(ResponseEntity.ok(buyer));
        when(restTemplate.getForEntity(any(String.class), eq(Cart[].class))).thenReturn(ResponseEntity.ok(new Cart[]{cartItem}));
        when(restTemplate.getForEntity(any(String.class), eq(Product.class))).thenReturn(ResponseEntity.ok(product));
        when(orderService.createOrder(any(Buyer.class), any(Double.class), any(String.class))).thenReturn(new Orders());
        doNothing().when(orderdetailService).addOrderDetails(any(Order_Detail.class));
        // Call the method under test
        String result = orderController.confirmCheckout(paymentMethod, request, model);

        // Verify results
        verify(emailService).sendEmail(eq(buyer.getEmail()), any(String.class), any(String.class));

    }
    
    @Test
    public void testConfirmCheckout_NotifySeller() throws Exception {
        // Setup mock data
        Long buyerId = 1L;
        String paymentMethod = "Credit Card";
        Buyer buyer = new Buyer();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");
        buyer.setStreet("123 Main St");
        buyer.setCity("Springfield");
        buyer.setState("IL");
        buyer.setEmail("john.doe@example.com");

        Cart cartItem = new Cart();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        // Create a product with low stock
        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(100.0);
        product.setDiscountPrice(10.0);
        product.setQuantity(1); // Low stock to trigger notification
        product.setImage("image.jpg");
        product.setProductName("Product 1");
        product.setSellerId(2L); // Assume sellerId is 2

        // Mock Seller information
        Seller seller = new Seller();
        seller.setFirstName("Alice");
        seller.setLastName("Smith");
        seller.setEmail("alice.smith@example.com");

        // Mock responses
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("buyerId", String.valueOf(buyerId))});
        when(restTemplate.getForEntity(any(String.class), eq(Buyer.class))).thenReturn(ResponseEntity.ok(buyer));
        when(restTemplate.getForEntity(any(String.class), eq(Cart[].class))).thenReturn(ResponseEntity.ok(new Cart[]{cartItem}));
        when(restTemplate.getForEntity(any(String.class), eq(Product.class))).thenReturn(ResponseEntity.ok(product));
        when(restTemplate.getForEntity(contains("/seller/sellerController"), eq(Seller.class))).thenReturn(ResponseEntity.ok(seller));
        when(orderService.createOrder(any(Buyer.class), any(Double.class), any(String.class))).thenReturn(new Orders());
        doNothing().when(orderdetailService).addOrderDetails(any(Order_Detail.class));

        // Call the method under test
        String result = orderController.confirmCheckout(paymentMethod, request, model);
    }
    
    
    @Test
    public void testGetOrdersByBuyer() {
        // Setup mock data
        Long buyerId = 1L;

        // Mock Buyer Orders
        Orders order = new Orders();
        order.setTransaction_id(1L);

        List<Orders> ordersList = Arrays.asList(order);

        // Mock Order Detail
        Order_Detail orderDetail = new Order_Detail();
        orderDetail.setProductId(2L);
        orderDetail.setStatus("Delivered");

        List<Order_Detail> orderDetailsList = Arrays.asList(orderDetail);

        // Mock Product
        Product product = new Product();
        product.setProductId(2L);
        product.setProductName("Product 2");
        product.setImage("product_image.jpg");
        product.setPrice(99.99);

        // Mock Response Entities
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("buyerId", String.valueOf(buyerId))});
        when(orderService.getOrdersByBuyerId(buyerId)).thenReturn(ordersList);
        when(orderdetailService.getOrderDetailByOrderId(order.getTransaction_id())).thenReturn(orderDetailsList);
        when(restTemplate.getForEntity(any(String.class), eq(Product.class))).thenReturn(ResponseEntity.ok(product));
        when(reviewService.existsByCustomerAndProduct(buyerId, orderDetail.getProductId())).thenReturn(true);

        // Create a mock Model
        when(model.addAttribute(any(String.class), any())).thenReturn(model);

        // Call the method under test
        String viewName = orderController.getOrdersByBuyer(model, request);

    }
    
    

    @Test
    public void testShowCompletedOrders_NoSellerId() {
        // Setup mock request without cookies
        when(request.getCookies()).thenReturn(new Cookie[0]);

        // Call the method under test
        String viewName = orderController.showCompletedOrders(model, request);

        // Verify redirection to login page
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }
    @Test
    public void testShowCompletedOrders_Success() {
        // Setup mock sellerId from cookies
        Long sellerId = 1L;
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sellerId", String.valueOf(sellerId))});

        // Mock Order_Detail
        Order_Detail orderDetail = new Order_Detail();
        orderDetail.setProductId(1L);
        orderDetail.setQuantity(2);
        orderDetail.setPrice_per_unit(100.0);
        orderDetail.setStatus("Completed");

        Orders order = new Orders();
        order.setOrderId(100L);
        order.setOrderDate(LocalDate.now());
        order.setBuyerId(1L);
        orderDetail.setOrder(order);

        List<Order_Detail> orderDetails = Arrays.asList(orderDetail);
        when(orderdetailService.getOrdersBySellerId(eq(sellerId))).thenReturn(orderDetails);

        // Mock Product response
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Product 1");
        product.setImage("image.jpg");
        when(restTemplate.getForEntity(any(String.class), eq(Product.class))).thenReturn(ResponseEntity.ok(product));

        // Mock Buyer response
        Buyer buyer = new Buyer();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");
        buyer.setStreet("123 Main St");
        buyer.setCity("Springfield");
        buyer.setState("IL");
        when(restTemplate.getForEntity(any(String.class), eq(Buyer.class))).thenReturn(ResponseEntity.ok(buyer));

        // Call the method under test
        String result = orderController.showCompletedOrders(model, request);

        // Verify the interaction and results
        verify(orderdetailService).getOrdersBySellerId(sellerId);
        verify(restTemplate).getForEntity(contains("/products/cartController/1"), eq(Product.class));
        verify(restTemplate).getForEntity(contains("/ecom/sellerController?buyerId=1"), eq(Buyer.class));

        // Check if the received orders are added to the model
        verify(model).addAttribute(eq("orders"), anyList());
        assertEquals("Seller_CompletedOrder", result);
    }



    @Test
    public void testShowCompletedOrders_NoOrders() {
        // Setup mock sellerId from cookies
        Long sellerId = 1L;
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sellerId", String.valueOf(sellerId))});

        // Mock no orders for the seller
        when(orderdetailService.getOrdersBySellerId(eq(sellerId))).thenReturn(Collections.emptyList());

        // Call the method under test
        String result = orderController.showCompletedOrders(model, request);

        // Verify the interaction and results
        verify(orderdetailService).getOrdersBySellerId(sellerId);
        verify(model).addAttribute(eq("orders"), anyList());
        assertEquals("Seller_CompletedOrder", result);
    }
    
    
    @Test
    public void testUpdateOrderStatus_Success() {
        // Mock input parameters
        Long orderId = 1L;
        String status = "Shipped";

        // Mock request and service call
        doNothing().when(orderdetailService).updateOrderStatus(orderId, status);

        // Call the method under test
        String result = orderController.updateOrderStatus(orderId, status, model, request);

        // Verify that the service method was called
        verify(orderdetailService).updateOrderStatus(orderId, status);

        // Verify the redirect behavior after successful update
        assertEquals("redirect:http://localhost:8080/order/receivedOrders", result);
    }

    @Test
    public void testUpdateOrderStatus_Failure() {
        // Mock input parameters
        Long orderId = 1L;
        String status = "Shipped";

        // Simulate an exception in the service call
        doThrow(new RuntimeException("Database error")).when(orderdetailService).updateOrderStatus(orderId, status);

        // Call the method under test
        String result = orderController.updateOrderStatus(orderId, status, model, request);

        // Verify that the service method was called and threw an exception
        verify(orderdetailService).updateOrderStatus(orderId, status);

        // Verify that the error message was set in the model
        verify(model).addAttribute(eq("errorMessage"), eq("Failed to update order status."));

        // The method should not return a redirect URL due to failure
        assertNull(result);
    }

    
    
    @Test
    public void testShowProduct_WithReviews_Success() {
        // Mock input
        Long productId = 1L;

        // Mock Review data
        Review review1 = new Review();
        review1.setRating(4);
        review1.setComment("Great product");
        review1.setBuyerId(1L);

        Review review2 = new Review();
        review2.setRating(5);
        review2.setComment("Excellent!");
        review2.setBuyerId(2L);

        List<Review> reviews = Arrays.asList(review1, review2);

        // Mock service call to return reviews
        when(reviewService.getReviewsByProductId(productId)).thenReturn(reviews);

        // Mock Buyer data for both reviews
        Buyer buyer1 = new Buyer();
        buyer1.setFirstName("John");
        buyer1.setLastName("Doe");

        Buyer buyer2 = new Buyer();
        buyer2.setFirstName("Jane");
        buyer2.setLastName("Smith");

        when(restTemplate.getForEntity(any(String.class), eq(Buyer.class)))
            .thenReturn(ResponseEntity.ok(buyer1), ResponseEntity.ok(buyer2));

        // Call the method under test
        ResponseEntity<ReviewProducts> response = orderController.showProduct(productId);

        // Verify service and restTemplate calls
        verify(reviewService).getReviewsByProductId(productId);
        verify(restTemplate, times(2)).getForEntity(any(String.class), eq(Buyer.class));

        // Verify the response body
        ReviewProducts reviewProducts = response.getBody();
        assertNotNull(reviewProducts);
        assertEquals(2, reviewProducts.getReviewCount());
        assertEquals(4.5, reviewProducts.getAverageRating(), 0.01); // (4 + 5) / 2
        assertEquals(1, reviewProducts.getStarCounts()[3]); // 4-star count
        assertEquals(1, reviewProducts.getStarCounts()[4]); // 5-star count

        // Verify review details for buyers
        assertEquals("John Doe", reviewProducts.getReviews().get(0).getName());
        assertEquals("Jane Smith", reviewProducts.getReviews().get(1).getName());
    }

    @Test
    public void testShowProduct_NoReviews() {
        // Mock input
        Long productId = 1L;

        // Mock service call to return an empty list of reviews
        when(reviewService.getReviewsByProductId(productId)).thenReturn(Collections.emptyList());

        // Call the method under test
        ResponseEntity<ReviewProducts> response = orderController.showProduct(productId);

        // Verify service call
        verify(reviewService).getReviewsByProductId(productId);

        // Verify the response body
        ReviewProducts reviewProducts = response.getBody();
        assertNotNull(reviewProducts);
        assertEquals(0, reviewProducts.getReviewCount());
        assertEquals(0, reviewProducts.getAverageRating(), 0.01);
        assertArrayEquals(new int[5], reviewProducts.getStarCounts());
        assertTrue(reviewProducts.getReviews().isEmpty());
    }
    @Test
    public void testShowProduct_BuyerNotFound() {
        // Mock input
        Long productId = 1L;

        // Mock Review data
        Review review1 = new Review();
        review1.setRating(4);
        review1.setComment("Great product");
        review1.setBuyerId(1L);

        List<Review> reviews = Collections.singletonList(review1);

        // Mock service call to return reviews
        when(reviewService.getReviewsByProductId(productId)).thenReturn(reviews);

        // Simulate exception when fetching Buyer data
        when(restTemplate.getForEntity(any(String.class), eq(Buyer.class)))
            .thenThrow(new RuntimeException("Buyer not found"));

        // Call the method under test and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderController.showProduct(productId);
        });

        // Verify service call
        verify(reviewService).getReviewsByProductId(productId);
        verify(restTemplate).getForEntity(any(String.class), eq(Buyer.class));

        // Verify exception message
        assertEquals("Buyer not found", exception.getMessage());
    }
    
    
    @Test
    public void testCheckout_Success() {
        // Mock buyerId from cookies
        Long buyerId = 1L;
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("buyerId", String.valueOf(buyerId))});

        // Mock cart items
        Cart cartItem = new Cart();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        cartItem.setPrice(100.0);

        Cart[] cartArray = {cartItem};
        when(restTemplate.getForEntity(anyString(), eq(Cart[].class)))
                .thenReturn(ResponseEntity.ok(cartArray));

        // Mock product details
        Product product = new Product();
        product.setProductId(1L);
        product.setDiscountPrice(10.0);
        product.setPrice(90.0);

        when(restTemplate.getForEntity(anyString(), eq(Product.class)))
                .thenReturn(ResponseEntity.ok(product));

        // Mock buyer details
        Buyer buyer = new Buyer();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");

        when(restTemplate.getForEntity(anyString(), eq(Buyer.class)))
                .thenReturn(ResponseEntity.ok(buyer));

        // Call the method under test
        String viewName = orderController.checkout(model, request);

        // Verify method interactions
        verify(restTemplate).getForEntity(anyString(), eq(Cart[].class));
        verify(restTemplate).getForEntity(anyString(), eq(Product.class));
        verify(restTemplate).getForEntity(anyString(), eq(Buyer.class));

        // Verify that the view is correct
        assertEquals("checkout", viewName);

        // Verify the model attributes
        verify(model).addAttribute(eq("cartItems"), anyList());
        verify(model).addAttribute(eq("totalPrice"), anyDouble());
        verify(model).addAttribute(eq("buyer"), eq(buyer));
    }
    @Test
    public void testCheckout_NoBuyerLoggedIn() {
        // No cookies set, meaning buyerId is null
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // Call the method under test
        String viewName = orderController.checkout(model, request);

        // Verify that no service calls are made
        verify(restTemplate, never()).getForEntity(anyString(), eq(Cart[].class));
        verify(restTemplate, never()).getForEntity(anyString(), eq(Product.class));
        verify(restTemplate, never()).getForEntity(anyString(), eq(Buyer.class));

        // Verify that the view is correct (it should still try to render the checkout page)
        assertEquals("checkout", viewName);

        // Verify that no model attributes are set
        verify(model, never()).addAttribute(eq("cartItems"), any());
        verify(model, never()).addAttribute(eq("totalPrice"), any());
        verify(model, never()).addAttribute(eq("buyer"), any());
    }
    @Test
    public void testCheckout_EmptyCart() {
        // Mock buyerId from cookies
        Long buyerId = 1L;
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("buyerId", String.valueOf(buyerId))});

        // Mock empty cart
        Cart[] cartArray = {};
        when(restTemplate.getForEntity(anyString(), eq(Cart[].class)))
                .thenReturn(ResponseEntity.ok(cartArray));

        // Mock buyer details
        Buyer buyer = new Buyer();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");

        when(restTemplate.getForEntity(anyString(), eq(Buyer.class)))
                .thenReturn(ResponseEntity.ok(buyer));

        // Call the method under test
        String viewName = orderController.checkout(model, request);

        // Verify service and restTemplate calls
        verify(restTemplate).getForEntity(anyString(), eq(Cart[].class));
        verify(restTemplate).getForEntity(anyString(), eq(Buyer.class));

        // Verify that the view is correct
        assertEquals("checkout", viewName);

        // Verify the model attributes
        verify(model).addAttribute(eq("cartItems"), anyList());
        verify(model).addAttribute(eq("totalPrice"), anyDouble());
        verify(model).addAttribute(eq("buyer"), eq(buyer));
    }

    @Test
    public void testShowReceivedOrders_Success() {
        // Mock sellerId from cookies
        Long sellerId = 1L;
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sellerId", String.valueOf(sellerId))});

        // Mock order details
        Orders order = new Orders();
        order.setOrderId(1L);
        order.setOrderDate(LocalDate.now());
        order.setBuyerId(1L);
        Order_Detail orderDetail = new Order_Detail();
        orderDetail.setOrder(order);
        orderDetail.setProductId(1L);
        orderDetail.setQuantity(2);
        orderDetail.setPrice_per_unit(100.0);
        orderDetail.setStatus("Shipped");

        

        List<Order_Detail> orderDetails = Collections.singletonList(orderDetail);
        when(orderdetailService.getOrdersBySellerId(eq(sellerId))).thenReturn(orderDetails);

        // Mock product details
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Product 1");
        product.setImage("image.jpg");
        when(restTemplate.getForEntity(anyString(), eq(Product.class)))
                .thenReturn(ResponseEntity.ok(product));

        // Mock buyer details
        Buyer buyer = new Buyer();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");
        buyer.setStreet("123 Main St");
        buyer.setCity("Springfield");
        buyer.setState("IL");
        when(restTemplate.getForEntity(anyString(), eq(Buyer.class)))
                .thenReturn(ResponseEntity.ok(buyer));

        // Call the method under test
        String viewName = orderController.showReceivedOrders(model, request);

        // Verify method interactions
        verify(orderdetailService).getOrdersBySellerId(eq(sellerId));
        verify(restTemplate).getForEntity(anyString(), eq(Product.class));
        verify(restTemplate).getForEntity(anyString(), eq(Buyer.class));

        // Assert the correct view is returned
        assertEquals("Seller_ReceivedOrder", viewName);

        // Verify that the model attributes are set correctly
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("orders"), captor.capture());

        List<ReceivedOrders> orders = captor.getValue();
        assertEquals(1, orders.size());
        ReceivedOrders receivedOrder = orders.get(0);
        assertEquals(1L, receivedOrder.getOrderId());
        assertEquals("Product 1", receivedOrder.getProductName());
        assertEquals("John Doe", receivedOrder.getName());
        assertEquals("123 Main St\nSpringfield\nIL", receivedOrder.getAddress());
    }
    

    
    
    






    


   
    
    
    
}