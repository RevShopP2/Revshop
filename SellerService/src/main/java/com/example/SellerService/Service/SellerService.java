package com.example.SellerService.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SellerService.Model.Seller;
import com.example.SellerService.Repository.SellerRepository;
import com.example.SellerService.Utils.PasswordUtils;



@Service
public class SellerService {
	@Autowired
	private SellerRepository sellerRepo;
	@Autowired
	private PasswordUtils pwd_obj;
	public void insertSeller(Seller seller) throws NoSuchAlgorithmException {
		seller.setPassword(pwd_obj.hashPassword(seller.getPassword()));
		sellerRepo.save(seller);
	}

	public void deleteSeller(Long sellerId) {
		sellerRepo.deleteById(sellerId);
	}

	public Seller updateSeller(Long sellerId, Seller updatedSeller) {
		Optional<Seller> existingSeller = sellerRepo.findById(sellerId);

		if (existingSeller.isPresent()) {
			Seller seller = existingSeller.get();
			seller.setFirstName(updatedSeller.getFirstName());
			seller.setLastName(updatedSeller.getLastName());
			seller.setEmail(updatedSeller.getEmail());
			seller.setMobileNumber(updatedSeller.getMobileNumber());
			seller.setPassword(updatedSeller.getPassword());
			seller.setBussinessName(updatedSeller.getBussinessName());
			seller.setStreet(updatedSeller.getStreet());
			seller.setCity(updatedSeller.getCity());
			seller.setPostalCode(updatedSeller.getPostalCode());
			seller.setState(updatedSeller.getState());
			seller.setCountry(updatedSeller.getCountry());

			return sellerRepo.save(seller);
		}

		return null;
	}
	public void updateSellerPassword(String email, String newPassword) throws NoSuchAlgorithmException {
	    // Find the seller by email
	    Seller seller = sellerRepo.findByEmail(email);
	    System.out.println(seller);
	    if (seller != null) {
	        // Hash the new password before saving
	        seller.setPassword(newPassword);
	        sellerRepo.save(seller);
	        System.out.println("========");// Save the updated seller to the database
	    }
	}

	public Seller getSellerDetailsById(Long sellerId) {
        return sellerRepo.findById(sellerId).orElse(null);
    }

    // Update seller profile information
    public void updateSellerProfile(Seller seller) {
        sellerRepo.save(seller);
    }
	public Seller getSellerDetailsByEmail(String email) {
		return sellerRepo.findByEmail(email);
	}

	public Seller getSellerDetailsByMobileNumber(String mobileNumber) {

		return sellerRepo.findByMobileNumber(mobileNumber);
	}

	

	public Seller findById(Long double1) {
		// TODO Auto-generated method stub
		return sellerRepo.findById(double1).get();
	}
}