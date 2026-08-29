package com.shopsmart.entity;

public enum OrderStatus {
    PENDING,        // Created, awaiting confirmation
    CONFIRMED,      // Confirmed, stock reserved
    PROCESSING,     // Being prepared/packed
    COMPLETED,      // Fully paid, stock released (sale finalized)
    CANCELLED,      // Cancelled, stock released
    REFUNDED        // Refunded after completion
}