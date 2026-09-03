package com.shopsmart.dto.response;

import com.shopsmart.entity.CustomerType;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private CustomerType type;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
    private Integer loyaltyPoints;
}