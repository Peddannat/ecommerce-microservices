package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User Service — Handles registration, authentication and user profiles.
 * Issues JWT tokens on successful login.
 * Running at: http://localhost:8081
 */
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
