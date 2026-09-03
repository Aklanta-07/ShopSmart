package com.shopsmart.dto.response;

import com.shopsmart.entity.OrderStatus;
import com.shopsmart.entity.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderResponse {

    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerPhone;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}