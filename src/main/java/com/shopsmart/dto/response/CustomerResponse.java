package com.shopsmart.dto.response;

import com.shopsmart.entity.CustomerType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;

    private String phone;

    private String email;

    private String name;

    private CustomerType type;

    private CustomerGroupResponse group;

    private Integer loyaltyPoints;

    private BigDecimal creditLimit;

    private BigDecimal creditUsed;

    private BigDecimal availableCredit;

    private String gstNumber;

    private Boolean isActive;

    private List<CustomerAddressResponse> addresses;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}