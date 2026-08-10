package com.shopsmart.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummary {
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer quantityAvailable;
    private Integer reorderLevel;
    private Integer maxStockLevel;
    private String location;
    private Boolean isLowStock;
    private Boolean isOutOfStock;
    private LocalDateTime lastRestockedAt;
    private LocalDateTime lastCountedAt;
}