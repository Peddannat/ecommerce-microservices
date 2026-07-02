package com.peddannat.ecommerce.controller;

import com.peddannat.ecommerce.dto.request.AddCartItemRequest;
import com.peddannat.ecommerce.dto.request.UpdateCartItemQuantityRequest;
import com.peddannat.ecommerce.dto.response.ApiResponse;
import com.peddannat.ecommerce.dto.response.CartResponse;
import com.peddannat.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddCartItemRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        cartService.addItemToCart(userId, request),
                        "Item added to cart successfully"
                ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(cartService.getCartByUserId(userId), "Cart fetched successfully")
        );
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        cartService.updateCartItemQuantity(userId, productId, request),
                        "Cart item updated successfully"
                )
        );
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        cartService.removeItemFromCart(userId, productId),
                        "Cart item removed successfully"
                )
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }
}