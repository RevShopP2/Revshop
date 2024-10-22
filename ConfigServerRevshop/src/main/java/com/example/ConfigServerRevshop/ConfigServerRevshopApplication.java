package com.example.ConfigServerRevshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerRevshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerRevshopApplication.class, args);
	}

}
