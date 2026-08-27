package com.shopsmart.controller;

import com.shopsmart.dto.request.CustomerRequest;
import com.shopsmart.dto.response.CustomerResponse;
import com.shopsmart.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse created = customerService.create(request);
        return ResponseEntity.created(
                java.net.URI.create("/api/customers/" + created.getId()))
                .body(created);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Get customer by phone (quick lookup)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponse> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(customerService.getByPhone(phone));
    }

    @GetMapping
    @Operation(summary = "Search customers with pagination")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(customerService.search(keyword, pageable));
        }
        return ResponseEntity.ok(customerService.getAll(pageable));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get customers by type (WALK_IN, REGULAR, WHOLESALE)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerResponse>> getByType(
            @PathVariable String type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.getByType(type, pageable));
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get customers by group")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerResponse>> getByGroupId(
            @PathVariable Long groupId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.getByGroupId(groupId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @PatchMapping("/{id}/loyalty")
    @Operation(summary = "Update customer loyalty points")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> updateLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        return ResponseEntity.ok(customerService.updateLoyaltyPoints(id, points));
    }

    @PatchMapping("/{id}/credit-limit")
    @Operation(summary = "Update customer credit limit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> updateCreditLimit(
            @PathVariable Long id,
            @RequestParam BigDecimal creditLimit) {
        return ResponseEntity.ok(customerService.updateCreditLimit(id, creditLimit));
    }

    @PatchMapping("/{id}/credit-used")
    @Operation(summary = "Update customer credit used (for orders)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerResponse> updateCreditUsed(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(customerService.updateCreditUsed(id, amount));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete customer (deactivate)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate a deactivated customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> reactivate(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.reactivate(id));
    }

    @GetMapping("/low-credit")
    @Operation(summary = "Get customers with low available credit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<CustomerResponse>> getLowCreditCustomers() {
        return ResponseEntity.ok(customerService.getCustomersWithLowCredit());
    }

    @GetMapping("/count")
    @Operation(summary = "Get total active customer count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Long> getTotalActiveCustomers() {
        return ResponseEntity.ok(customerService.getTotalActiveCustomers());
    }
}