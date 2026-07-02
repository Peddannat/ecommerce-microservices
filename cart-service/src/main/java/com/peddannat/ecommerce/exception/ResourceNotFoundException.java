package com.peddannat.ecommerce.exception;

/**
 * Thrown when cart or product-related resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}