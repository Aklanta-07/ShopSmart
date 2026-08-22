package com.shopsmart.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }

    public CustomerNotFoundException(String phone) {
        super("Customer not found with phone: " + phone);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}