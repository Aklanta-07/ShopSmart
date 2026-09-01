package com.shopsmart.dto.request;

import com.shopsmart.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchRequest {

    private String keyword;           // order number, customer name, phone
    private Long customerId;
    private OrderStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "desc";
}