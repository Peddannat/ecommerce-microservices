package com.peddannat.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
/**
 * Eureka Server — Service Registry for Peddannat E-Commerce Platform.
 *
 * All microservices (user, product, order, inventory, gateway)
 * register themselves here on startup and discover each other
 * through this registry.
 *
 * Dashboard available at: http://localhost:8761
 * Health check at:        http://localhost:8761/actuator/health
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
