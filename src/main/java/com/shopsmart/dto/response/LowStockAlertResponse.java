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
public class LowStockAlertResponse {
    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private Integer quantityAvailable;
    private Integer reorderLevel;
    private Integer shortage; // reorderLevel - quantityAvailable
    private String location;
    private LocalDateTime lastRestockedAt;
}