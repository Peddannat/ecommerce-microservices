package com.peddannat.ecommerce.exception;

/**
 * Thrown when inventory is not found for a given product.
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
