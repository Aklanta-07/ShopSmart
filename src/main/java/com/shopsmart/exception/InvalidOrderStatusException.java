package com.shopsmart.exception;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String currentStatus, String requiredStatus) {
        super("Invalid order status transition: current=" + currentStatus + ", required=" + requiredStatus);
    }

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}