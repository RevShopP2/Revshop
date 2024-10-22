package com.example.BuyerMicroService;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.example.BuyerMicroService.controller.ForgotPasswordController;
import com.example.BuyerMicroService.model.Buyer;
import com.example.BuyerMicroService.service.BuyerService;
import com.example.BuyerMicroService.service.ForgotService;
import com.example.BuyerMicroService.utils.PasswordUtils;

class ForgotPasswordControllerTest {

    @Mock
    private ForgotService forgotService;

    @Mock
    private BuyerService buyerService;

    @Mock
    private PasswordUtils pwd_obj;

    @Mock
    private Model model;

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    private final String email = "test@example.com";
    private String verificationCode = String.format("%06d", new Random().nextInt(999999));
    private final String newPassword = "newPassword123";
    private final String confirmPassword = "newPassword123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendVerificationCode_Success() {
        Buyer buyer = new Buyer();
        buyer.setEmail(email);

        when(buyerService.getBuyerDetailsByEmail(email)).thenReturn(buyer);
        doNothing().when(forgotService).sendVerificationEmail(anyString(), anyString());

        ResponseEntity<String> response = forgotPasswordController.sendVerificationCode(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Verification Code Sent Successfully", response.getBody());
    }

    @Test
    void testSendVerificationCode_Failure() {
        when(buyerService.getBuyerDetailsByEmail(email)).thenReturn(null);

        ResponseEntity<String> response = forgotPasswordController.sendVerificationCode(email);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Check your Email"));
    }


    
    @Test
    void testVerifyCode_Success() {
        // Generate the verification code and simulate sending it
        verificationCode = String.format("%06d", new Random().nextInt(999999));
        forgotPasswordController.sendVerificationCode(email);

        // Store the generated verification code in the controller's storage
        forgotPasswordController.verifyCodeStorage.put(email, verificationCode);

        // Now verify the code
        ResponseEntity<String> response = forgotPasswordController.verifyCode(verificationCode);

        // Assert the response is successful
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Code verified successfully.", response.getBody());
    }

    @Test
    void testVerifyCode_Failure() {
        ResponseEntity<String> response = forgotPasswordController.verifyCode("wrongCode");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid verification code.", response.getBody());
    }

    @Test
    void testResetPassword_Success() throws NoSuchAlgorithmException {
        forgotPasswordController.sendVerificationCode(email);

        when(pwd_obj.hashPassword(newPassword)).thenReturn("hashedPassword");
        doNothing().when(buyerService).updateBuyerPassword(anyString(), anyString());

        String result = forgotPasswordController.resetPassword(newPassword, confirmPassword, model);

        assertEquals("LoginPage", result);
    }

    @Test
    void testResetPassword_Failure_Mismatch() throws NoSuchAlgorithmException {
        String result = forgotPasswordController.resetPassword("newPass1", "newPass2", model);

        assertEquals("ForgotPassword", result);
        verify(model).addAttribute("error", "Passwords do not match.");
    }
}

