package com.shopsmart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.shopsmart.entity.ProductUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String barcode;
    private String description;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal taxRate;
    private ProductUnit unit;
    private Integer reorderLevel;
    private Integer maxStockLevel;
    private Boolean isActive;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Embedded inventory summary
    private InventorySummary inventory;
}