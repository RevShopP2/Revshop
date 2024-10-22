package com.example.BuyerMicroService;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.BuyerMicroService.service.EmailService;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateOtp() {
        String otp = emailService.generateOtp();
        
        // Verify that OTP is 6 digits long
        assertEquals(6, otp.length());
    }

    @Test
    void testSendEmail() {
        String email = "test@example.com";
        String otp = "123456";

        // Use doNothing() for void methods
        doNothing().when(mailSender).send(new SimpleMailMessage());
        
        boolean result = emailService.sendEmail(email, otp);
        
        // Verify that email was sent and OTP was stored
        assertTrue(result);
        verify(mailSender, times(1)).send(new SimpleMailMessage() {{
            setTo(email);
            setSubject("Your OTP Code");
            setText("Your OTP for registration is: " + otp);
        }});
    }

    @Test
    void testVerifyOtp() {
        String email = "test@example.com";
        String otp = "123456";
        
        emailService.sendEmail(email, otp);  // Sending OTP to store it
        boolean isOtpValid = emailService.verifyOtp(email, otp);
        
        assertTrue(isOtpValid);  // Check if OTP verification passes
    }
}
