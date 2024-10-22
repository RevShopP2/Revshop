package com.example.SellerService.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
    private JavaMailSender javaMailSender;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();  // Thread-safe storage for OTPs

    // Method to generate random OTP
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);  // Generate 6 digit OTP
        return String.valueOf(otp);
    }

    // Method to send email
    public boolean sendEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP for registration is: " + otp);

            javaMailSender.send(message);

            // Store OTP for later verification
            otpStorage.put(toEmail, otp);
            return true;
        } catch (MailException e) {
            e.printStackTrace(); // Log the exception for debugging
            return false;
        }
    }

    // Method to verify OTP
    public boolean verifyOtp(String email, String inputOtp) {
        String storedOtp = otpStorage.get(email);
        
        return storedOtp != null && storedOtp.equals(inputOtp);
    }
	 
}