package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inventory Service — Manages product stock information.
 *
 * Responsibilities:
 * - Maintain total quantity
 * - Maintain reserved quantity
 * - Calculate available quantity
 * - Support stock updates for order workflow
 *
 * Running at: http://localhost:8084
 */
@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
