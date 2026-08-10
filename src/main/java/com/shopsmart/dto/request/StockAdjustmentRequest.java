package com.shopsmart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StockAdjustmentRequest {

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType type;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    @Size(max = 100, message = "Reference must not exceed 100 characters")
    private String reference; // e.g., PO number, invoice number
}