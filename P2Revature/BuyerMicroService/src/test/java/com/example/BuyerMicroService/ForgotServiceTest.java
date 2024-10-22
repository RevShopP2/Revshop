package com.example.BuyerMicroService;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.BuyerMicroService.service.ForgotService;

public class ForgotServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ForgotService forgotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


}
