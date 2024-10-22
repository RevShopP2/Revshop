package com.example.BuyerMicroService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.example.BuyerMicroService.model.Buyer;

@Repository
public interface BuyerRepository  extends JpaRepository<Buyer,Long>{

	Buyer findByEmail(String email);

	Buyer findByMobileNumber(String mobileNumber);
}
	


