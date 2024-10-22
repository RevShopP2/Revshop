package com.example.BuyerMicroService.service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.BuyerMicroService.model.Buyer;
import com.example.BuyerMicroService.repository.BuyerRepository;
import com.example.BuyerMicroService.utils.PasswordUtils;

@Service
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepo;

    @Autowired
    private PasswordUtils pwd_obj;

    // Insert Buyer with password hashing
    public void insertBuyer(Buyer buyer) throws NoSuchAlgorithmException {
        buyer.setPassword(pwd_obj.hashPassword(buyer.getPassword()));
        buyerRepo.save(buyer);
    }

    // Delete Buyer by ID
    public void deleteBuyer(Long buyerId) {
        buyerRepo.deleteById(buyerId);
    }

    // Update Buyer by ID with Optional handling
    public Buyer updateBuyer(Long buyerId, Buyer updatedBuyer) {
        Optional<Buyer> existingBuyer = buyerRepo.findById(buyerId);

        if (existingBuyer.isPresent()) {
            Buyer buyer = existingBuyer.get();
            buyer.setFirstName(updatedBuyer.getFirstName());
            buyer.setLastName(updatedBuyer.getLastName());
            buyer.setEmail(updatedBuyer.getEmail());
            buyer.setMobileNumber(updatedBuyer.getMobileNumber());
            buyer.setStreet(updatedBuyer.getStreet());
            buyer.setCity(updatedBuyer.getCity());
            buyer.setPostalCode(updatedBuyer.getPostalCode());
            buyer.setState(updatedBuyer.getState());
            buyer.setCountry(updatedBuyer.getCountry());
            return buyerRepo.save(buyer);
        }
        return null; // Return null if Buyer not found
    }

    // Get Buyer by Email with logging
    public Buyer getBuyerDetailsByEmail(String email) {
        Buyer buyer = buyerRepo.findByEmail(email);
        return buyer;
    }

    // Get Buyer by Mobile Number
    public Buyer getBuyerDetailsByMobileNumber(String mobileNumber) {
        return buyerRepo.findByMobileNumber(mobileNumber);
    }

    // Update Buyer Password with hashing
    public void updateBuyerPassword(String email, String newPassword) throws NoSuchAlgorithmException {
        Buyer buyer = buyerRepo.findByEmail(email);
        if (buyer != null) {
            buyer.setPassword(newPassword); // Hash the password before saving
            buyerRepo.save(buyer);
        }
    }

    // Get Buyer by ID with Optional handling
    public Buyer getBuyerDetailsById(Long buyerId) {
        return buyerRepo.findById(buyerId).orElse(null); // Return null if not found
    }

    // Update Buyer Profile
    public void updateBuyerProfile(Buyer updatedBuyer) {
        Optional<Buyer> existingBuyer = buyerRepo.findById(updatedBuyer.getBuyerId());
        if (existingBuyer.isPresent()) {
            Buyer buyer = existingBuyer.get();
            buyer.setFirstName(updatedBuyer.getFirstName());
            buyer.setLastName(updatedBuyer.getLastName());
            buyer.setEmail(updatedBuyer.getEmail());
            buyer.setMobileNumber(updatedBuyer.getMobileNumber());
            // Add more fields if needed

            buyerRepo.save(buyer);
        }
    }
}
