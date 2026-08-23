package com.shopsmart.service;

import com.shopsmart.dto.request.CustomerGroupRequest;
import com.shopsmart.dto.response.CustomerGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerGroupService {

    CustomerGroupResponse create(CustomerGroupRequest request);

    CustomerGroupResponse getById(Long id);

    CustomerGroupResponse getByName(String name);

    Page<CustomerGroupResponse> search(String keyword, Pageable pageable);

    Page<CustomerGroupResponse> getAll(Pageable pageable);

    CustomerGroupResponse update(Long id, CustomerGroupRequest request);

    void delete(Long id);

    List<CustomerGroupResponse> getActiveGroups();
}