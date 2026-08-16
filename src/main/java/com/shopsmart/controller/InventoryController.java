package com.shopsmart.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsmart.dto.request.StockAdjustmentRequest;
import com.shopsmart.dto.response.InventoryResponse;
import com.shopsmart.dto.response.InventorySummaryResponse;
import com.shopsmart.service.InventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory and stock management")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory for a product")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProductId(productId));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust stock (increase/decrease/set)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventoryResponse> adjustStock(
            @RequestParam Long productId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(inventoryService.adjustStock(productId, request));
    }

    @PostMapping("/restock")
    @Operation(summary = "Restock product")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventoryResponse> restock(
            @RequestParam Long productId,
            @RequestParam @jakarta.validation.constraints.Min(1) int quantity,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(inventoryService.restock(productId, quantity, location));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock for order")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventoryResponse> reserveStock(
            @RequestParam Long productId,
            @RequestParam @jakarta.validation.constraints.Min(1) int quantity) {
        return ResponseEntity.ok(inventoryService.reserveStock(productId, quantity));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventoryResponse> releaseReservedStock(
            @RequestParam Long productId,
            @RequestParam @jakarta.validation.constraints.Min(1) int quantity) {
        return ResponseEntity.ok(inventoryService.releaseReservedStock(productId, quantity));
    }

    @PatchMapping("/product/{productId}/reorder-level")
    @Operation(summary = "Update reorder level")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryResponse> updateReorderLevel(
            @PathVariable Long productId,
            @RequestParam @jakarta.validation.constraints.Min(0) int reorderLevel,
            @RequestParam(required = false) @jakarta.validation.constraints.Min(0) Integer maxStockLevel) {
        return ResponseEntity.ok(inventoryService.updateReorderLevel(productId, reorderLevel, maxStockLevel));
    }

    @PatchMapping("/product/{productId}/location")
    @Operation(summary = "Update storage location")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryResponse> updateLocation(
            @PathVariable Long productId,
            @RequestParam String location) {
        return ResponseEntity.ok(inventoryService.updateLocation(productId, location));
    }

    @PostMapping("/product/{productId}/count")
    @Operation(summary = "Record physical stock count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventoryResponse> recordStockCount(
            @PathVariable Long productId,
            @RequestParam @jakarta.validation.constraints.Min(0) int countedQuantity) {
        return ResponseEntity.ok(inventoryService.recordStockCount(productId, countedQuantity));
    }

    @GetMapping("/alerts/low-stock")
    @Operation(summary = "Get low stock alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<InventoryResponse>> getLowStockAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts(page, size));
    }

    @GetMapping("/alerts/out-of-stock")
    @Operation(summary = "Get out of stock alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<InventoryResponse>> getOutOfStockAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inventoryService.getOutOfStockAlerts(page, size));
    }

    @GetMapping
    @Operation(summary = "Get all inventory (paginated)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<InventoryResponse>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(inventoryService.getAllInventory(page, size, sortBy, sortDir));
    }

    @GetMapping("/location/{location}")
    @Operation(summary = "Get inventory by location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<InventoryResponse>> getByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inventoryService.getByLocation(location, page, size));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get inventory summary dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventorySummaryResponse> getInventorySummary() {
        return ResponseEntity.ok(inventoryService.getInventorySummary());
    }
}