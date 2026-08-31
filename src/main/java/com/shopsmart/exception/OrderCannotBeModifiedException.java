package com.shopsmart.exception;

public class OrderCannotBeModifiedException extends RuntimeException {

    public OrderCannotBeModifiedException(String orderNumber, String status) {
        super("Order " + orderNumber + " cannot be modified in status: " + status);
    }

    public OrderCannotBeModifiedException(String message) {
        super(message);
    }
}