package com.example.BuyerMicroService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.BuyerMicroService.model.Buyer;
import com.example.BuyerMicroService.repository.BuyerRepository;
import com.example.BuyerMicroService.service.BuyerService;

@ExtendWith(MockitoExtension.class)
class BuyerServiceUpdateTest {

    @Mock
    private BuyerRepository buyerRepo;

    @InjectMocks
    private BuyerService buyerService;

    @BeforeEach
    void setUp() {
        // Initialize the mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateBuyer() {
        // Arrange
        Buyer existingBuyer = new Buyer();
        existingBuyer.setBuyerId(1L);
        existingBuyer.setFirstName("OldFirstName");

        Buyer updatedBuyer = new Buyer();
        updatedBuyer.setFirstName("NewFirstName");

        // Mock the repository to return the existing buyer
        when(buyerRepo.findById(1L)).thenReturn(Optional.of(existingBuyer));

        // Act
        buyerService.updateBuyer(1L, updatedBuyer);

        // Assert
        verify(buyerRepo).save(existingBuyer);  // Verify that save was called
        assertEquals("NewFirstName", existingBuyer.getFirstName());  // Check that the first name was updated
    }
}
