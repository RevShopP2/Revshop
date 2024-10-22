package com.example.ServiceDiscoveryRevshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceDiscoveryRevshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceDiscoveryRevshopApplication.class, args);
	}

}
