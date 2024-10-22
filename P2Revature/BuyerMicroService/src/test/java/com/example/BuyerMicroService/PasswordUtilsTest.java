package com.example.BuyerMicroService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.BuyerMicroService.utils.PasswordUtils;

public class PasswordUtilsTest {

    private PasswordUtils passwordUtils;

    @BeforeEach
    void setUp() {
        passwordUtils = new PasswordUtils();
    }

    @Test
    void testHashPassword() throws NoSuchAlgorithmException {
        String password = "myPassword";
        String hashedPassword = passwordUtils.hashPassword(password);
        
        // Verify that the hashed password is not null or empty and of a certain length (SHA-256 produces 64 hex characters)
        assertEquals(64, hashedPassword.length());
    }


}
