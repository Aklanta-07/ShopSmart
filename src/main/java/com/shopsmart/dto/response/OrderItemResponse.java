package com.shopsmart.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal lineTotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
}