package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway — Single entry point for Peddannat E-Commerce Platform.
 *
 * Responsibilities:
 * - JWT authentication for all protected routes
 * - Request routing to downstream microservices via Eureka load balancing
 * - Public routes: /api/users/register, /api/users/login
 *
 * Running at: http://localhost:8080
 */

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
