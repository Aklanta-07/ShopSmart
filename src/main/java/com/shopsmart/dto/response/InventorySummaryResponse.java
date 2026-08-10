package com.shopsmart.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponse {
    private Long totalProducts;
    private Long totalStockItems;
    private Long lowStockCount;
    private Long outOfStockCount;
    private BigDecimal totalStockValue;
    private List<LowStockAlertResponse> topLowStockItems;
}