package com.shopsmart.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one order item is required")
    private List<CreateOrderItemRequest> items;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private BigDecimal taxAmount;

    @Data
    @Builder
    @NoArgsConstructor  
    @AllArgsConstructor
    public static class CreateOrderItemRequest {

        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
        private BigDecimal unitPrice;

        @DecimalMin(value = "0.0", message = "Discount percentage must be non-negative")
        @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
        @Builder.Default
        private BigDecimal discountPercentage = BigDecimal.ZERO;

        @DecimalMin(value = "0.0", message = "Tax rate must be non-negative")
        @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100")
        @Builder.Default
        private BigDecimal taxRate = BigDecimal.ZERO;
    }
}