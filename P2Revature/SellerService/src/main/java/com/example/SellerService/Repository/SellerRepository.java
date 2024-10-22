package com.example.SellerService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SellerService.Model.Seller;


public interface SellerRepository extends JpaRepository<Seller,Long>{

	Seller findByEmail(String email);

	Seller findByMobileNumber(String mobileNumber);

}