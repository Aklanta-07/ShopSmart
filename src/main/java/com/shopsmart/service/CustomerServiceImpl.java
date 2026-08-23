package com.shopsmart.service;

import com.shopsmart.dto.request.CustomerRequest;
import com.shopsmart.dto.request.CustomerAddressRequest;
import com.shopsmart.dto.response.CustomerResponse;
import com.shopsmart.dto.response.CustomerGroupResponse;
import com.shopsmart.dto.response.CustomerAddressResponse;
import com.shopsmart.entity.Customer;
import com.shopsmart.entity.CustomerAddress;
import com.shopsmart.entity.CustomerGroup;
import com.shopsmart.entity.CustomerType;
import com.shopsmart.exception.CustomerNotFoundException;
import com.shopsmart.exception.CustomerGroupNotFoundException;
import com.shopsmart.exception.DuplicatePhoneException;
import com.shopsmart.exception.DuplicateEmailException;
import com.shopsmart.repository.CustomerRepository;
import com.shopsmart.repository.CustomerGroupRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerGroupRepository customerGroupRepository;

    @Override
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicatePhoneException(request.getPhone());
        }
        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        CustomerGroup group = null;
        if (request.getGroupId() != null) {
            group = customerGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new CustomerGroupNotFoundException(request.getGroupId()));
        }

        Customer customer = Customer.builder()
                .phone(request.getPhone())
                .email(request.getEmail())
                .name(request.getName())
                .type(request.getType() != null ? request.getType() : CustomerType.WALK_IN)
                .group(group)
                .loyaltyPoints(0)
                .creditLimit(request.getCreditLimit())
                .creditUsed(BigDecimal.ZERO)
                .gstNumber(request.getGstNumber())
                .isActive(request.getIsActive())
                .build();

        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            List<CustomerAddress> addresses = request.getAddresses().stream()
                    .map(addrReq -> toAddressEntity(addrReq, customer))
                    .collect(Collectors.toList());
            customer.setAddresses(addresses);
        }

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new CustomerNotFoundException(phone));
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> search(String keyword, Pageable pageable) {
        return customerRepository.search(keyword, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerRepository.findByIsActiveTrue(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getByType(String type, Pageable pageable) {
        CustomerType customerType = CustomerType.valueOf(type.toUpperCase());
        return customerRepository.findByType(customerType).stream()
                .map(this::toResponse)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getByGroupId(Long groupId, Pageable pageable) {
        return customerRepository.findByGroupId(groupId, pageable).map(this::toResponse);
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!customer.getPhone().equals(request.getPhone()) && customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicatePhoneException(request.getPhone());
        }
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        CustomerGroup group = null;
        if (request.getGroupId() != null) {
            group = customerGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new CustomerGroupNotFoundException(request.getGroupId()));
        }

        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        customer.setType(request.getType() != null ? request.getType() : customer.getType());
        customer.setGroup(group);
        customer.setCreditLimit(request.getCreditLimit());
        customer.setGstNumber(request.getGstNumber());
        customer.setIsActive(request.getIsActive());

        if (request.getAddresses() != null) {
            customer.getAddresses().clear();
            request.getAddresses().forEach(addrReq -> {
                CustomerAddress addr = toAddressEntity(addrReq, customer);
                customer.getAddresses().add(addr);
            });
        }

        return toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateLoyaltyPoints(Long id, Integer points) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setLoyaltyPoints(points);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCreditLimit(Long id, BigDecimal creditLimit) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setCreditLimit(creditLimit);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCreditUsed(Long id, BigDecimal amount) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        BigDecimal newUsed = (customer.getCreditUsed() != null ? customer.getCreditUsed() : BigDecimal.ZERO).add(amount);
        if (customer.getCreditLimit() != null && newUsed.compareTo(customer.getCreditLimit()) > 0) {
            throw new IllegalArgumentException("Credit limit exceeded");
        }
        customer.setCreditUsed(newUsed);
        return toResponse(customerRepository.save(customer));
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setIsActive(false);
        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersWithLowCredit() {
        return customerRepository.findByIsActiveTrue().stream()
                .filter(c -> c.getCreditLimit() != null && c.getAvailableCredit().compareTo(BigDecimal.valueOf(1000)) < 0)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalActiveCustomers() {
        return customerRepository.countByIsActiveTrue();
    }

    private CustomerAddress toAddressEntity(CustomerAddressRequest req, Customer customer) {
        return CustomerAddress.builder()
                .customer(customer)
                .type(req.getType())
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .city(req.getCity())
                .state(req.getState())
                .pincode(req.getPincode())
                .country(req.getCountry())
                .isDefault(req.getIsDefault())
                .isActive(true)
                .build();
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerGroupResponse groupResponse = null;
        if (customer.getGroup() != null) {
            groupResponse = CustomerGroupResponse.builder()
                    .id(customer.getGroup().getId())
                    .name(customer.getGroup().getName())
                    .description(customer.getGroup().getDescription())
                    .discountPercentage(customer.getGroup().getDiscountPercentage())
                    .isActive(customer.getGroup().getIsActive())
                    .createdAt(customer.getGroup().getCreatedAt())
                    .updatedAt(customer.getGroup().getUpdatedAt())
                    .build();
        }

        List<CustomerAddressResponse> addressResponses = customer.getAddresses().stream()
                .filter(CustomerAddress::getIsActive)
                .map(addr -> CustomerAddressResponse.builder()
                        .id(addr.getId())
                        .type(addr.getType())
                        .addressLine1(addr.getAddressLine1())
                        .addressLine2(addr.getAddressLine2())
                        .city(addr.getCity())
                        .state(addr.getState())
                        .pincode(addr.getPincode())
                        .country(addr.getCountry())
                        .isDefault(addr.getIsDefault())
                        .isActive(addr.getIsActive())
                        .createdAt(addr.getCreatedAt())
                        .updatedAt(addr.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return CustomerResponse.builder()
                .id(customer.getId())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .name(customer.getName())
                .type(customer.getType())
                .group(groupResponse)
                .loyaltyPoints(customer.getLoyaltyPoints())
                .creditLimit(customer.getCreditLimit())
                .creditUsed(customer.getCreditUsed())
                .availableCredit(customer.getAvailableCredit())
                .gstNumber(customer.getGstNumber())
                .isActive(customer.getIsActive())
                .addresses(addressResponses)
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}