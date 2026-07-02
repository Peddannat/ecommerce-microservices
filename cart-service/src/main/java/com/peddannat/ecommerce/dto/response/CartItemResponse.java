package com.peddannat.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Response DTO for each item inside a cart.
 */
@Getter
@AllArgsConstructor
public class CartItemResponse {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal subTotal;
}