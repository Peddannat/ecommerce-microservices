package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Service — Manages order placement and lifecycle.
 *
 * Responsibilities:
 * - Place orders with stock validation via Feign clients
 * - Confirm, cancel orders and restore inventory on cancellation
 * - Fetch orders by user or by order id
 *
 * Running at: http://localhost:8083
 */
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
