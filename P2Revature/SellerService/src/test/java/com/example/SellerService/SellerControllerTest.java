package com.example.SellerService;


import com.example.SellerService.Controller.SellerController;
import com.example.SellerService.Model.Seller;
import com.example.SellerService.Service.EmailService;
import com.example.SellerService.Service.SellerService;
import com.example.SellerService.Utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;

public class SellerControllerTest {

    @InjectMocks
    private SellerController sellerController;

    @Mock
    private SellerService sellerService;

    @Mock
    private PasswordUtils passwordUtils;

    @Mock
    private EmailService emailService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    
    private Seller testSeller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Seller testSeller = new Seller(); // Initialize a test seller object
        testSeller.setEmail("test@example.com");
        testSeller.setSellerId(1L);
    }

    // Test for the registration form view
    @Test
    public void testRegistrationForm() {
        // Arrange
        when(model.addAttribute("seller", new Seller())).thenReturn(model);
        
        // Act
        String viewName = sellerController.registrationForm(model);
        
        // Assert
        assertEquals("SellerRegistration", viewName);
    }

    // Test for handling registration
    @Test
    public void testRegistration() throws Exception {
        // Arrange
        Seller seller = new Seller();
        seller.setEmail("test@example.com");
        seller.setPassword("password");
        seller.setFirstName("John");
        seller.setLastName("Doe");

        doNothing().when(sellerService).insertSeller(any(Seller.class));

        // Act
        String result = sellerController.registration(seller);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
        verify(sellerService).insertSeller(seller);
    }

    // Additional tests can be added here for other methods...
    
    
    @Test
    void testSendVerificationEmail_Success() {
        when(emailService.generateOtp()).thenReturn("123456");
        when(emailService.sendEmail("test@example.com", "123456")).thenReturn(true);

        ResponseEntity<String> result = sellerController.sendVerificationEmail("test@example.com");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("OTP sent successfully.", result.getBody());
    }

    @Test
    void testSendVerificationEmail_Failure() {
        when(emailService.generateOtp()).thenReturn("123456");
        when(emailService.sendEmail("test@example.com", "123456")).thenReturn(false);

        ResponseEntity<String> result = sellerController.sendVerificationEmail("test@example.com");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Failed to send OTP.", result.getBody());
    }

    @Test
    void testVerifyOtp_Success() {
        when(emailService.verifyOtp("test@example.com", "123456")).thenReturn(true);

        ResponseEntity<String> result = sellerController.verifyOtp("test@example.com", "123456");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("OTP verified successfully.", result.getBody());
    }

    @Test
    void testVerifyOtp_Failure() {
        when(emailService.verifyOtp("test@example.com", "123456")).thenReturn(false);

        ResponseEntity<String> result = sellerController.verifyOtp("test@example.com", "123456");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid OTP.", result.getBody());
    }
    
    
    
    
    
    @Test
    public void testSellerLogin_SuccessByEmail() throws NoSuchAlgorithmException {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        Seller seller = new Seller();
        seller.setSellerId(1L);
        seller.setEmail(email);
        seller.setPassword(password);

        // Mock the service method to return the seller object
        when(sellerService.getSellerDetailsByEmail(email)).thenReturn(seller);
        when(passwordUtils.hashPassword(password)).thenReturn(password); // Simulate password hashing

        // Mock the model
        when(model.addAttribute("sellerId", seller.getSellerId())).thenReturn(model);
        
        // Act
        String result = sellerController.sellerLogin(email, null, password, model, response);

        // Assert
        assertEquals("SellerDashboard", result);
        verify(response).addCookie(any(Cookie.class)); // Verify that a cookie is added
        verify(model).addAttribute("sellerId", seller.getSellerId());
    }

    @Test
    public void testSellerLogin_FailureByEmail() throws NoSuchAlgorithmException {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";

        // Mock the service method to return null (no seller found)
        when(sellerService.getSellerDetailsByEmail(email)).thenReturn(null);

        // Act
        String result = sellerController.sellerLogin(email, null, password, model, response);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
        verify(model).addAttribute("errorMessage", "Invalid Email or Password...\nIf you are a new user kindly register to access our services.");
    }

    @Test
    public void testSellerLogin_SuccessByMobileNumber() throws NoSuchAlgorithmException {
        // Arrange
        String mobileNumber = "1234567890";
        String password = "password";
        Seller seller = new Seller();
        seller.setSellerId(2L);
        seller.setMobileNumber(mobileNumber);
        seller.setPassword(password);

        // Mock the service method to return the seller object
        when(sellerService.getSellerDetailsByMobileNumber(mobileNumber)).thenReturn(seller);
        when(passwordUtils.hashPassword(password)).thenReturn(password); // Simulate password hashing

        // Mock the model
        when(model.addAttribute("sellerId", seller.getSellerId())).thenReturn(model);
        
        // Act
        String result = sellerController.sellerLogin(null, mobileNumber, password, model, response);

        // Assert
        assertEquals("SellerDashboard", result);
        verify(response).addCookie(any(Cookie.class)); // Verify that a cookie is added
        verify(model).addAttribute("sellerId", seller.getSellerId());
    }

    @Test
    public void testSellerLogin_FailureByMobileNumber() throws NoSuchAlgorithmException {
        // Arrange
        String mobileNumber = "1234567890";
        String password = "wrongpassword";

        // Mock the service method to return null (no seller found)
        when(sellerService.getSellerDetailsByMobileNumber(mobileNumber)).thenReturn(null);

        // Act
        String result = sellerController.sellerLogin(null, mobileNumber, password, model, response);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
        verify(model).addAttribute("errorMessage", "Invalid Email or Password...\nIf you are a new user kindly register to access our services.");
    }
    
    
    
    @Test
    public void testShowDashboard_Success() {
        // Arrange
        Long sellerId = 1L;
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setEmail("test@example.com");

        // Mock the cookies to contain the sellerId
        Cookie cookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        
        // Mock the service method to return the seller object
        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(seller);

        // Act
        String result = sellerController.showDashboard(request, model);

        // Assert
        assertEquals("SellerDashboard", result);
        verify(model).addAttribute("seller", seller);
    }

    @Test
    public void testShowDashboard_MissingSellerIdCookie() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String result = sellerController.showDashboard(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
    }

    @Test
    public void testShowDashboard_InvalidSellerIdInCookie() {
        // Arrange
        Cookie cookie = new Cookie("sellerId", "invalid");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // Act
        String result = sellerController.showDashboard(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
    }

    @Test
    public void testShowDashboard_SellerNotFound() {
        // Arrange
        Long sellerId = 1L;
        Cookie cookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(null);

        // Act
        String result = sellerController.showDashboard(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
    }
    
    @Test
    public void testGetSellerIdFromCookies_Found() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("sellerId", "123"),
            new Cookie("otherCookie", "value")
        };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        Long sellerId = sellerController.getSellerIdFromCookies(request);

        // Assert
        assertNotNull(sellerId);
        assertEquals(123L, sellerId);
    }
    
    @Test
    public void testRegBuyer() {
        // Act
        String result = sellerController.regBuyer(model);

        // Assert
        
        assertEquals("buyersellerreg", result);
    }
    
    
    @Test
    public void testViewProfile_SellerIdCookieFound() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("sellerId", "1")
        };
        when(request.getCookies()).thenReturn(cookies);
        when(sellerService.getSellerDetailsById(1L)).thenReturn(testSeller);


    }

    @Test
    public void testViewProfile_SellerIdCookieNotFound() {
        // Arrange
        Cookie[] cookies = new Cookie[] {}; // No cookies present
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String result = sellerController.viewProfile(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
    }

    @Test
    public void testViewProfile_InvalidSellerId() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("sellerId", "invalid") // Invalid seller ID
        };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String result = sellerController.viewProfile(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
    }

    @Test
    public void testViewProfile_SellerNotFound() {
        // Arrange
        Cookie[] cookies = new Cookie[] {
            new Cookie("sellerId", "1")
        };
        when(request.getCookies()).thenReturn(cookies);
        when(sellerService.getSellerDetailsById(1L)).thenReturn(null); // Seller not found

        // Act
        String result = sellerController.viewProfile(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", result);
    }
    
    
    @Test
    public void testUpdateProfileInfo_Success() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String mobileNumber = "1234567890";
        Long sellerId = 1L;
        
        // Simulate cookie
        Cookie sellerCookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{sellerCookie});

        // Mock the existing seller
        Seller existingSeller = new Seller();
        existingSeller.setSellerId(sellerId);
        existingSeller.setFirstName("OldFirstName");
        existingSeller.setLastName("OldLastName");
        existingSeller.setEmail("old.email@example.com");
        existingSeller.setMobileNumber("0987654321");
        
        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(existingSeller);

        // Act
        String viewName = sellerController.updateProfileInfo(firstName, lastName, email, mobileNumber, request, response, model);

        // Assert
        verify(sellerService).updateSellerProfile(existingSeller);
        assertEquals(firstName, existingSeller.getFirstName());
        assertEquals(lastName, existingSeller.getLastName());
        assertEquals(email, existingSeller.getEmail());
        assertEquals(mobileNumber, existingSeller.getMobileNumber());
        assertEquals("sellerprofile", viewName);
        verify(model).addAttribute("seller", existingSeller);
    }

    @Test
    public void testUpdateProfileInfo_SellerIdNotFound() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String viewName = sellerController.updateProfileInfo("John", "Doe", "john.doe@example.com", "1234567890", request, response, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }

    @Test
    public void testUpdateProfileInfo_SellerNotFound() {
        // Arrange
        Long sellerId = 1L;
        Cookie sellerCookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{sellerCookie});
        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(null);

        // Act
        String viewName = sellerController.updateProfileInfo("John", "Doe", "john.doe@example.com", "1234567890", request, response, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }
    
    @Test
    public void testUpdateAddress_Success() {
        // Arrange
        Long sellerId = 1L;
        String street = "123 Main St";
        String city = "Springfield";
        String state = "IL";
        int postalCode = 62704;
        String country = "USA";

        // Simulate cookie
        Cookie sellerCookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{sellerCookie});

        // Mock the existing seller
        Seller existingSeller = new Seller();
        existingSeller.setSellerId(sellerId);
        existingSeller.setStreet("Old Street");
        existingSeller.setCity("Old City");
        existingSeller.setState("Old State");
        existingSeller.setPostalCode(12345);
        existingSeller.setCountry("Old Country");

        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(existingSeller);

        // Act
        String viewName = sellerController.updateAddress(street, city, state, postalCode, country, request, response, model);

        // Assert
        verify(sellerService).updateSellerProfile(existingSeller);
        assertEquals(street, existingSeller.getStreet());
        assertEquals(city, existingSeller.getCity());
        assertEquals(state, existingSeller.getState());
        assertEquals(postalCode, existingSeller.getPostalCode());
        assertEquals(country, existingSeller.getCountry());
        assertEquals("sellerprofile", viewName);
        verify(model).addAttribute("seller", existingSeller);
    }

    @Test
    public void testUpdateAddress_SellerIdNotFound() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String viewName = sellerController.updateAddress("Street", "City", "State", 12345, "Country", request, response, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }

    @Test
    public void testUpdateAddress_SellerNotFound() {
        // Arrange
        Long sellerId = 1L;
        Cookie sellerCookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{sellerCookie});
        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(null);

        // Act
        String viewName = sellerController.updateAddress("Street", "City", "State", 12345, "Country", request, response, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }
    @Test
    public void testChangePassword_SellerIdNotFound() throws NoSuchAlgorithmException {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String viewName = sellerController.changePassword("currentPassword", "newPassword", request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }

    @Test
    public void testChangePassword_SellerNotFound() throws NoSuchAlgorithmException {
        // Arrange
        Long sellerId = 1L;
        Cookie sellerCookie = new Cookie("sellerId", sellerId.toString());
        when(request.getCookies()).thenReturn(new Cookie[]{sellerCookie});
        when(sellerService.getSellerDetailsById(sellerId)).thenReturn(null);

        // Act
        String viewName = sellerController.changePassword("currentPassword", "newPassword", request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }
    
    @Test
    public void testLogout() {
        // Act
        String viewName = sellerController.logout(response);

        // Assert
        Cookie cookie = new Cookie("sellerId", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        verify(response).addCookie(cookie);
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }

    @Test
    public void testShowSellerProducts_SellerIdNotFound() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act
        String viewName = sellerController.showSellerProducts(request, model);

        // Assert
        assertEquals("redirect:http://localhost:8080/ecom/LoginPage", viewName);
    }

    @Test
    public void testShowSellerProducts_Success() {
        // Arrange
        String sellerId = "1";
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sellerId", sellerId)});

        // Act
        String viewName = sellerController.showSellerProducts(request, model);

        // Assert
        String expectedRedirectUrl = "http://localhost:8080/products/manage?sellerId=" + sellerId;
        assertEquals("redirect:" + expectedRedirectUrl, viewName);
    }

    @Test
    public void testGetSellerObj_Success() {
        // Arrange
        Long sellerId = 1L;
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        when(sellerService.findById(sellerId)).thenReturn(seller);

        // Act
        ResponseEntity<Seller> responseEntity = sellerController.getSellerObj(sellerId);

        // Assert
        assertEquals(ResponseEntity.ok(seller), responseEntity);
    }

    @Test
    public void testGetSellerObj_SellerNotFound() {
        // Arrange
        Long sellerId = 1L;
        when(sellerService.findById(sellerId)).thenReturn(null);

        // Act
        ResponseEntity<Seller> responseEntity = sellerController.getSellerObj(sellerId);

        // Assert
        assertEquals(ResponseEntity.ok().build(), responseEntity); // Assuming it returns a 200 OK even if not found
    }
    
    


}
