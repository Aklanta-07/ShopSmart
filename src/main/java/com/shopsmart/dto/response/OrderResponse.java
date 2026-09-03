package com.shopsmart.dto.response;

import com.shopsmart.entity.OrderStatus;
import com.shopsmart.entity.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private CustomerSummaryResponse customer;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private String notes;
    private LocalDateTime completedAt;
    private List<OrderItemResponse> items;
    private List<PaymentResponse> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}