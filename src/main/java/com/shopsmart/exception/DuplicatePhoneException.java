package com.shopsmart.exception;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String phone) {
        super("Customer already exists with phone: " + phone);
    }
}