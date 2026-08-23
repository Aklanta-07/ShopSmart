package com.shopsmart.service;

import com.shopsmart.dto.request.CustomerGroupRequest;
import com.shopsmart.dto.response.CustomerGroupResponse;
import com.shopsmart.entity.CustomerGroup;
import com.shopsmart.exception.CustomerGroupNotFoundException;
import com.shopsmart.exception.DuplicateCustomerGroupNameException;
import com.shopsmart.repository.CustomerGroupRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerGroupServiceImpl implements CustomerGroupService {

    private final CustomerGroupRepository customerGroupRepository;

    @Override
    public CustomerGroupResponse create(CustomerGroupRequest request) {
        if (customerGroupRepository.existsByName(request.getName())) {
            throw new DuplicateCustomerGroupNameException(request.getName());
        }

        CustomerGroup group = CustomerGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .discountPercentage(request.getDiscountPercentage())
                .isActive(request.getIsActive())
                .build();

        return toResponse(customerGroupRepository.save(group));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerGroupResponse getById(Long id) {
        CustomerGroup group = customerGroupRepository.findById(id)
                .orElseThrow(() -> new CustomerGroupNotFoundException(id));
        return toResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerGroupResponse getByName(String name) {
        CustomerGroup group = customerGroupRepository.findByName(name)
                .orElseThrow(() -> new CustomerGroupNotFoundException(name));
        return toResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerGroupResponse> search(String keyword, Pageable pageable) {
        return customerGroupRepository.search(keyword, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerGroupResponse> getAll(Pageable pageable) {
        return customerGroupRepository.findByIsActiveTrue(pageable).map(this::toResponse);
    }

    @Override
    public CustomerGroupResponse update(Long id, CustomerGroupRequest request) {
        CustomerGroup group = customerGroupRepository.findById(id)
                .orElseThrow(() -> new CustomerGroupNotFoundException(id));

        if (!group.getName().equals(request.getName()) && customerGroupRepository.existsByName(request.getName())) {
            throw new DuplicateCustomerGroupNameException(request.getName());
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setDiscountPercentage(request.getDiscountPercentage());
        group.setIsActive(request.getIsActive());

        return toResponse(customerGroupRepository.save(group));
    }

    @Override
    public void delete(Long id) {
        CustomerGroup group = customerGroupRepository.findById(id)
                .orElseThrow(() -> new CustomerGroupNotFoundException(id));
        group.setIsActive(false);
        customerGroupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerGroupResponse> getActiveGroups() {
        return customerGroupRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CustomerGroupResponse toResponse(CustomerGroup group) {
        return CustomerGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .discountPercentage(group.getDiscountPercentage())
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}