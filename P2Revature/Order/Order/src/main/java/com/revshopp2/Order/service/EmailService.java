package com.revshopp2.Order.service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender javaMailSender;

	public void sendEmail(String to, String subject, String body) {
	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(body, true); // The second parameter 'true' indicates HTML content
	        
	        javaMailSender.send(message);
	    } catch (MessagingException e) {
	        e.printStackTrace();  // Handle the exception as needed
	    }
	}
	 
}