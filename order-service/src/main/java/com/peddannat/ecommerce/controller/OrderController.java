package com.peddannat.ecommerce.controller;

import com.peddannat.ecommerce.dto.request.OrderRequest;
import com.peddannat.ecommerce.dto.response.ApiResponse;
import com.peddannat.ecommerce.dto.response.OrderResponse;
import com.peddannat.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order endpoints.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@Valid @RequestBody OrderRequest request) {
        return new ResponseEntity<>(
                ApiResponse.success(orderService.placeOrder(request), "Order placed successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id), "Order fetched successfully"));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getOrdersByUserId(userId), "User orders fetched successfully")
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.cancelOrder(id), "Order cancelled successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getAllOrders(), "All orders fetched successfully")
        );
    }

}
