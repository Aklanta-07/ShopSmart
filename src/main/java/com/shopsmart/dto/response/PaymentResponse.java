package com.shopsmart.dto.response;

import com.shopsmart.entity.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String referenceNumber;
    private String receivedBy;
    private LocalDateTime paidAt;
}