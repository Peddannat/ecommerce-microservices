package com.peddannat.ecommerce.service.impl;

import com.peddannat.ecommerce.dto.request.AddCartItemRequest;
import com.peddannat.ecommerce.dto.request.UpdateCartItemQuantityRequest;
import com.peddannat.ecommerce.dto.response.ApiResponse;
import com.peddannat.ecommerce.dto.response.CartItemResponse;
import com.peddannat.ecommerce.dto.response.CartResponse;
import com.peddannat.ecommerce.dto.response.ProductResponse;
import com.peddannat.ecommerce.entity.Cart;
import com.peddannat.ecommerce.entity.CartItem;
import com.peddannat.ecommerce.exception.ResourceNotFoundException;
import com.peddannat.ecommerce.external.ProductClient;
import com.peddannat.ecommerce.repository.CartRepository;
import com.peddannat.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, AddCartItemRequest request) {
        log.info("Adding item to cart. userId={}, productId={}, quantity={}",
                userId, request.getProductId(), request.getQuantity());

        ProductResponse product = fetchActiveProduct(request.getProductId());

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return newCart;
                });

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setProductName(product.getName());
            existingItem.setPrice(product.getPrice());
            existingItem.setImageUrl(product.getImageUrl());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setImageUrl(product.getImageUrl());
            newItem.setQuantity(request.getQuantity());
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        log.info("Fetching cart for userId={}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long userId, Long productId, UpdateCartItemQuantityRequest request) {
        log.info("Updating cart item quantity. userId={}, productId={}, quantity={}",
                userId, productId, request.getQuantity());

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItem item = cart.getItems()
                .stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found for product id: " + productId
                ));

        ProductResponse product = fetchActiveProduct(productId);

        item.setQuantity(request.getQuantity());
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
        item.setImageUrl(product.getImageUrl());

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        log.info("Removing item from cart. userId={}, productId={}", userId, productId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItem item = cart.getItems()
                .stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product id: " + productId));

        cart.removeItem(item);

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for userId={}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private ProductResponse fetchActiveProduct(Long productId) {
        ApiResponse<ProductResponse> response = productClient.getProductById(productId);

        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        ProductResponse product = response.getData();

        if (!product.isActive()) {
            throw new IllegalStateException("Product is inactive with id: " + productId);
        }

        return product;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems()
                .stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getImageUrl(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                itemResponses,
                totalAmount,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}