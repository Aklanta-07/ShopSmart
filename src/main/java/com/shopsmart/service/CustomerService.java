package com.shopsmart.service;

import com.shopsmart.dto.request.CustomerRequest;
import com.shopsmart.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

    CustomerResponse getById(Long id);

    CustomerResponse getByPhone(String phone);

    Page<CustomerResponse> search(String keyword, Pageable pageable);

    Page<CustomerResponse> getAll(Pageable pageable);

    Page<CustomerResponse> getByType(String type, Pageable pageable);

    Page<CustomerResponse> getByGroupId(Long groupId, Pageable pageable);

    CustomerResponse update(Long id, CustomerRequest request);

    CustomerResponse updateLoyaltyPoints(Long id, Integer points);

    CustomerResponse updateCreditLimit(Long id, BigDecimal creditLimit);

    CustomerResponse updateCreditUsed(Long id, BigDecimal amount);

    void delete(Long id);

    CustomerResponse reactivate(Long id);

    List<CustomerResponse> getCustomersWithLowCredit();

    Long getTotalActiveCustomers();
}