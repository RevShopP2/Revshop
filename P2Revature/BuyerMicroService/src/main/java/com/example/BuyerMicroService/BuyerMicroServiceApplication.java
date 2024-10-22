package com.example.BuyerMicroService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BuyerMicroServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyerMicroServiceApplication.class, args);
	}

}
