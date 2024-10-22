package com.example.SellerService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.SellerService.Model.Seller;
import com.example.SellerService.Repository.SellerRepository;
import com.example.SellerService.Service.SellerService;
import com.example.SellerService.Utils.PasswordUtils;

class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepo;

    @Mock
    private PasswordUtils pwd_obj;

    @InjectMocks
    private SellerService sellerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertSeller() throws NoSuchAlgorithmException {
        // Arrange
        Seller seller = new Seller();
        seller.setPassword("plainPassword");
        String hashedPassword = "hashedPassword";

        when(pwd_obj.hashPassword("plainPassword")).thenReturn(hashedPassword);
        when(sellerRepo.save(seller)).thenReturn(seller);

        // Act
        sellerService.insertSeller(seller);

        // Assert
        verify(pwd_obj, times(1)).hashPassword("plainPassword");
        assertEquals(hashedPassword, seller.getPassword());
        verify(sellerRepo, times(1)).save(seller);
    }

    @Test
    void testDeleteSeller() {
        // Arrange
        Long sellerId = 1L;

        // Act
        sellerService.deleteSeller(sellerId);

        // Assert
        verify(sellerRepo, times(1)).deleteById(sellerId);
    }

    @Test
    void testUpdateSeller() {
        // Arrange
        Long sellerId = 1L;
        Seller existingSeller = new Seller();
        existingSeller.setFirstName("John");
        existingSeller.setEmail("john@example.com");

        Seller updatedSeller = new Seller();
        updatedSeller.setFirstName("Jane");
        updatedSeller.setEmail("jane@example.com");

        when(sellerRepo.findById(sellerId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepo.save(existingSeller)).thenReturn(existingSeller);

        // Act
        Seller result = sellerService.updateSeller(sellerId, updatedSeller);

        // Assert
        assertEquals("Jane", result.getFirstName());
        assertEquals("jane@example.com", result.getEmail());
        verify(sellerRepo, times(1)).findById(sellerId);
        verify(sellerRepo, times(1)).save(existingSeller);
    }

    @Test
    void testUpdateSellerPassword() throws NoSuchAlgorithmException {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword";
        String hashedPassword = "hashedNewPassword";

        Seller seller = new Seller();
        seller.setEmail(email);

        when(sellerRepo.findByEmail(email)).thenReturn(seller);
        when(pwd_obj.hashPassword(newPassword)).thenReturn(hashedPassword);

        // Act
        sellerService.updateSellerPassword(email, newPassword);

        // Assert
        assertNotEquals(hashedPassword, seller.getPassword());

    }

    @Test
    void testGetSellerDetailsById() {
        // Arrange
        Long sellerId = 1L;
        Seller seller = new Seller();
        seller.setFirstName("John");

        when(sellerRepo.findById(sellerId)).thenReturn(Optional.of(seller));

        // Act
        Seller result = sellerService.getSellerDetailsById(sellerId);

        // Assert
        assertEquals("John", result.getFirstName());
        verify(sellerRepo, times(1)).findById(sellerId);
    }

    @Test
    void testUpdateSellerProfile() {
        // Arrange
        Seller seller = new Seller();
        seller.setFirstName("John");

        // Act
        sellerService.updateSellerProfile(seller);

        // Assert
        verify(sellerRepo, times(1)).save(seller);
    }

    @Test
    void testGetSellerDetailsByEmail() {
        // Arrange
        String email = "test@example.com";
        Seller seller = new Seller();
        seller.setEmail(email);

        when(sellerRepo.findByEmail(email)).thenReturn(seller);

        // Act
        Seller result = sellerService.getSellerDetailsByEmail(email);

        // Assert
        assertEquals(email, result.getEmail());
        verify(sellerRepo, times(1)).findByEmail(email);
    }

    @Test
    void testGetSellerDetailsByMobileNumber() {
        // Arrange
        String mobileNumber = "1234567890";
        Seller seller = new Seller();
        seller.setMobileNumber(mobileNumber);

        when(sellerRepo.findByMobileNumber(mobileNumber)).thenReturn(seller);

        // Act
        Seller result = sellerService.getSellerDetailsByMobileNumber(mobileNumber);

        // Assert
        assertEquals(mobileNumber, result.getMobileNumber());
        verify(sellerRepo, times(1)).findByMobileNumber(mobileNumber);
    }

    @Test
    void testFindById() {
        // Arrange
        Long sellerId = 1L;
        Seller seller = new Seller();
        when(sellerRepo.findById(sellerId)).thenReturn(Optional.of(seller));

        // Act
        Seller result = sellerService.findById(sellerId);

        // Assert
        assertEquals(seller, result);
        verify(sellerRepo, times(1)).findById(sellerId);
    }
}

