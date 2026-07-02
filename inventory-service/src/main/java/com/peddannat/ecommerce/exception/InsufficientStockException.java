package com.peddannat.ecommerce.exception;

/**
 * Thrown when requested stock is greater than available stock.
 */
public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String message) {
        super(message);
    }
}
