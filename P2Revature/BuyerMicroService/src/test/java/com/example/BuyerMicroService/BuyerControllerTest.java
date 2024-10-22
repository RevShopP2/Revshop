package com.example.BuyerMicroService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.example.BuyerMicroService.controller.BuyerController;
import com.example.BuyerMicroService.model.Buyer;
import com.example.BuyerMicroService.service.BuyerService;
import com.example.BuyerMicroService.service.EmailService;
import com.example.BuyerMicroService.utils.PasswordUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class BuyerControllerTest {

    @Mock
    private BuyerService buyerService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordUtils passwordUtils;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private BuyerController buyerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrationForm() {
        String result = buyerController.registrationForm(model);
        assertEquals("BuyerRegistration", result);
        verify(model).addAttribute(eq("buyer"), any(Buyer.class));
    }

    @Test
    void testRegistration() throws NoSuchAlgorithmException {
        Buyer buyer = new Buyer();
        doNothing().when(buyerService).insertBuyer(any(Buyer.class));

        String result = buyerController.registration(buyer);
        assertEquals("LoginPage", result);
        verify(buyerService).insertBuyer(buyer);
    }

    @Test
    void testSendVerificationEmail_Success() {
        when(emailService.generateOtp()).thenReturn("123456");
        when(emailService.sendEmail("test@example.com", "123456")).thenReturn(true);

        ResponseEntity<String> result = buyerController.sendVerificationEmail("test@example.com");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("OTP sent successfully.", result.getBody());
    }

    @Test
    void testSendVerificationEmail_Failure() {
        when(emailService.generateOtp()).thenReturn("123456");
        when(emailService.sendEmail("test@example.com", "123456")).thenReturn(false);

        ResponseEntity<String> result = buyerController.sendVerificationEmail("test@example.com");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Failed to send OTP.", result.getBody());
    }

    @Test
    void testVerifyOtp_Success() {
        when(emailService.verifyOtp("test@example.com", "123456")).thenReturn(true);

        ResponseEntity<String> result = buyerController.verifyOtp("test@example.com", "123456");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("OTP verified successfully.", result.getBody());
    }

    @Test
    void testVerifyOtp_Failure() {
        when(emailService.verifyOtp("test@example.com", "123456")).thenReturn(false);

        ResponseEntity<String> result = buyerController.verifyOtp("test@example.com", "123456");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid OTP.", result.getBody());
    }

    @Test
    void testBuyerLoginValidEmail() throws NoSuchAlgorithmException {
        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        buyer.setPassword("hashedPassword");

        when(buyerService.getBuyerDetailsByEmail("test@example.com")).thenReturn(buyer);
        when(passwordUtils.hashPassword("password")).thenReturn("hashedPassword");

        String result = buyerController.buyerLogin("test@example.com", null, "password", model, response);

        assertEquals("buyerdashboard", result);
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void testBuyerLoginInvalidPassword() throws NoSuchAlgorithmException {
        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        buyer.setPassword("hashedPassword");

        when(buyerService.getBuyerDetailsByEmail("test@example.com")).thenReturn(buyer);
        when(passwordUtils.hashPassword("wrongPassword")).thenReturn("wrongHashedPassword");

        String result = buyerController.buyerLogin("test@example.com", null, "wrongPassword", model, response);

        assertEquals("LoginPage", result);
        verify(model).addAttribute(eq("errorMessage"), any(String.class));
    }

    @Test
    void testViewProfile() {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("buyerId", "1");
        when(request.getCookies()).thenReturn(cookies);

        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        when(buyerService.getBuyerDetailsById(1L)).thenReturn(buyer);

        String result = buyerController.viewProfile(request, model);

        assertEquals("buyerprofile", result);
        verify(model).addAttribute("buyer", buyer);
    }

    @Test
    void testUpdateProfileInfo() {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("buyerId", "1");
        when(request.getCookies()).thenReturn(cookies);

        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        when(buyerService.getBuyerDetailsById(1L)).thenReturn(buyer);

        String result = buyerController.updateProfileInfo("John", "Doe", "test@example.com", "1234567890", request, response, model);

        assertEquals("buyerprofile", result);
        verify(buyerService).updateBuyerProfile(any(Buyer.class));
        verify(model).addAttribute(eq("buyer"), any(Buyer.class));
    }

    @Test
    void testUpdateAddress() {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("buyerId", "1");
        when(request.getCookies()).thenReturn(cookies);

        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        when(buyerService.getBuyerDetailsById(1L)).thenReturn(buyer);

        String result = buyerController.updateAddress("123 Main St", "City", "State", 12345, "Country", request, response, model);

        assertEquals("buyerprofile", result);
        verify(buyerService).updateBuyerProfile(any(Buyer.class));
        verify(model).addAttribute(eq("buyer"), any(Buyer.class));
    }

    @Test
    void testChangePasswordSuccess() throws NoSuchAlgorithmException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("buyerId", "1");
        when(request.getCookies()).thenReturn(cookies);

        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        buyer.setPassword("hashedPassword");
        when(buyerService.getBuyerDetailsById(1L)).thenReturn(buyer);
        when(passwordUtils.hashPassword("currentPassword")).thenReturn("hashedPassword");
        when(passwordUtils.hashPassword("newPassword")).thenReturn("newHashedPassword");

        String result = buyerController.changePassword("currentPassword", "newPassword", request, model);

        assertEquals("buyerprofile", result);
        verify(buyerService).updateBuyerProfile(any(Buyer.class));
        verify(model).addAttribute("successMessage", "Password updated successfully.");
    }

    @Test
    void testChangePasswordFailure() throws NoSuchAlgorithmException {
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("buyerId", "1");
        when(request.getCookies()).thenReturn(cookies);

        Buyer buyer = new Buyer();
        buyer.setBuyerId(1L);
        buyer.setPassword("hashedPassword");
        when(buyerService.getBuyerDetailsById(1L)).thenReturn(buyer);
        when(passwordUtils.hashPassword("currentPassword")).thenReturn("wrongHashedPassword");

        String result = buyerController.changePassword("currentPassword", "newPassword", request, model);

        assertEquals("buyerprofile", result);
        verify(model).addAttribute(eq("errorMessage"), any(String.class));
    }


    
    @Test
    void testLogoutMe() {
        // Mocking cookies
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("buyerId", "1");
        when(request.getCookies()).thenReturn(cookies);


        // Call the method under test
        String result = buyerController.logoutMe(request, response);

        // Validate the results
        assertEquals("welcomepage", result);
        
        // Verify that cookies are invalidated
        verify(response, times(1)).addCookie(any(Cookie.class));
        
    }
}
