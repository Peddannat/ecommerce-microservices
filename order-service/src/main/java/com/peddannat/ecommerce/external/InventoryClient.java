package com.peddannat.ecommerce.external;

import com.peddannat.ecommerce.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with inventory-service.
 */
@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

    // Checks whether sufficient stock exists for a product
    @GetMapping("/api/inventory/check/{productId}")
    ApiResponse<Boolean> checkStock(@PathVariable Long productId, @RequestParam int qty);

    // Reduces stock after successful order placement
    @PutMapping("/api/inventory/{productId}/reduce")
    ApiResponse<?>  reduceStock(@PathVariable Long productId, @RequestParam int qty);

    // Restores stock when a confirmed order is cancelled
    @PutMapping("/api/inventory/{productId}/add")
    ApiResponse<?> addStock(@PathVariable Long productId, @RequestParam int qty);

}
