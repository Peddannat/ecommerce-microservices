package com.peddannat.ecommerce.service;

import com.peddannat.ecommerce.dto.request.AddCartItemRequest;
import com.peddannat.ecommerce.dto.request.UpdateCartItemQuantityRequest;
import com.peddannat.ecommerce.dto.response.CartResponse;

/**
 * Service contract for cart operations.
 */
public interface CartService {

    CartResponse addItemToCart(Long userId, AddCartItemRequest request);

    CartResponse getCartByUserId(Long userId);

    CartResponse updateCartItemQuantity(Long userId, Long productId, UpdateCartItemQuantityRequest request);

    CartResponse removeItemFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}