package com.shopsmart.exception;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
    public ProductNotFoundException(String field, String value) {
        super("Product not found with " + field + ": " + value);
    }
}