package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Service — Manages product catalog operations.
 *
 * Responsibilities:
 * - Create products
 * - Update products
 * - Fetch products with pagination
 * - Filter products by category
 * - Soft delete products
 *
 * Running at: http://localhost:8082
 */
@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
