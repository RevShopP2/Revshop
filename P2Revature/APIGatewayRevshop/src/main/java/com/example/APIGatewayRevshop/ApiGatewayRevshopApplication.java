package com.example.APIGatewayRevshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayRevshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayRevshopApplication.class, args);
	}

}
