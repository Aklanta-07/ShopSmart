package com.shopsmart.exception;

public class CustomerGroupNotFoundException extends RuntimeException {

    public CustomerGroupNotFoundException(Long id) {
        super("Customer group not found with id: " + id);
    }

    public CustomerGroupNotFoundException(String name) {
        super("Customer group not found with name: " + name);
    }
}