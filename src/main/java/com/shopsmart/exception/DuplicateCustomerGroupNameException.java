package com.shopsmart.exception;

public class DuplicateCustomerGroupNameException extends RuntimeException {

    public DuplicateCustomerGroupNameException(String name) {
        super("Customer group already exists with name: " + name);
    }
}