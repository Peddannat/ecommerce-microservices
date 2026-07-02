package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Cart Service — Manages user cart operations.
 *
 * Responsibilities:
 * - Add items to cart
 * - Update cart item quantity
 * - Remove items from cart
 * - Fetch cart by user
 * - Clear cart after order placement
 *
 * Running at: http://localhost:8085
 */
@SpringBootApplication
@EnableFeignClients
public class CartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartServiceApplication.class, args);
	}

}
