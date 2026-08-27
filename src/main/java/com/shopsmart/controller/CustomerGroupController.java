package com.shopsmart.controller;

import com.shopsmart.dto.request.CustomerGroupRequest;
import com.shopsmart.dto.response.CustomerGroupResponse;
import com.shopsmart.service.CustomerGroupService;

import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

@RestController
@RequestMapping("/api/customer-groups")
@RequiredArgsConstructor
@Tag(name = "Customer Groups", description = "Customer group (pricing tier) management")
@SecurityRequirement(name = "bearerAuth")
public class CustomerGroupController {

    private final CustomerGroupService customerGroupService;

    @PostMapping
    @Operation(summary = "Create a new customer group")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerGroupResponse> create(@Valid @RequestBody CustomerGroupRequest request) {
        CustomerGroupResponse created = customerGroupService.create(request);
        return ResponseEntity.created(
                java.net.URI.create("/api/customer-groups/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer group by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerGroupService.getById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get customer group by name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CustomerGroupResponse> getByName(@PathVariable String name) {
        return ResponseEntity.ok(customerGroupService.getByName(name));
    }

    @GetMapping
    @Operation(summary = "Search customer groups with pagination")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<CustomerGroupResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(customerGroupService.search(keyword, pageable));
        }
        return ResponseEntity.ok(customerGroupService.getAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer group")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerGroupResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerGroupRequest request) {
        return ResponseEntity.ok(customerGroupService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete customer group (deactivate)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active customer groups (for dropdowns)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<CustomerGroupResponse>> getActiveGroups() {
        return ResponseEntity.ok(customerGroupService.getActiveGroups());
    }
}