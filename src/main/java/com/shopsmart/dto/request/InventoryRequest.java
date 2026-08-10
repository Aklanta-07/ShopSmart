package com.shopsmart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InventoryRequest {

    @Min(value = 0, message = "Quantity on hand cannot be negative")
    private Integer quantityOnHand;

    @Min(value = 0, message = "Quantity reserved cannot be negative")
    private Integer quantityReserved;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    private Integer maxStockLevel;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
}