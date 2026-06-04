package com.peddannat.ecommerce.controller;

import com.peddannat.ecommerce.dto.request.InventoryRequest;
import com.peddannat.ecommerce.dto.response.ApiResponse;
import com.peddannat.ecommerce.dto.response.InventoryResponse;
import com.peddannat.ecommerce.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Inventory created successfully"));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryByProductId(@PathVariable Long productId) {
        InventoryResponse response = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(response, "Inventory fetched successfully"));
    }

    @PutMapping("/{productId}/add")
    public ResponseEntity<ApiResponse<InventoryResponse>> addStock(
            @PathVariable Long productId,
            @RequestParam @Min(value = 1, message = "Quantity must be greater than 0") int qty) {

        InventoryResponse response = inventoryService.addStock(productId, qty);
        return ResponseEntity.ok(ApiResponse.success(response, "Stock added successfully"));
    }

    @PutMapping("/{productId}/reduce")
    public ResponseEntity<ApiResponse<InventoryResponse>> reduceStock(
            @PathVariable Long productId,
            @RequestParam @Min(value = 1, message = "Quantity must be greater than 0") int qty) {

        InventoryResponse response = inventoryService.reduceStock(productId, qty);
        return ResponseEntity.ok(ApiResponse.success(response, "Stock reduced successfully"));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkStock(
            @PathVariable Long productId,
            @RequestParam @Min(value = 1, message = "Quantity must be greater than 0") int qty) {

        boolean available = inventoryService.checkStock(productId, qty);
        return ResponseEntity.ok(ApiResponse.success(available, "Stock availability checked successfully"));
    }
}