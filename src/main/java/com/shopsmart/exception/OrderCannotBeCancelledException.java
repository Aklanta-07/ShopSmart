package com.shopsmart.exception;

public class OrderCannotBeCancelledException extends RuntimeException {

    public OrderCannotBeCancelledException(String orderNumber, String status) {
        super("Order " + orderNumber + " cannot be cancelled in status: " + status);
    }
}