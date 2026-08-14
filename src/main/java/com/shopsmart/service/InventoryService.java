package com.shopsmart.service;

import org.springframework.data.domain.Page;

import com.shopsmart.dto.request.StockAdjustmentRequest;
import com.shopsmart.dto.response.InventoryResponse;
import com.shopsmart.dto.response.InventorySummaryResponse;

public interface InventoryService {
    InventoryResponse getByProductId(Long productId);
    InventoryResponse adjustStock(Long productId, StockAdjustmentRequest request);
    InventoryResponse restock(Long productId, int quantity, String location);
    InventoryResponse reserveStock(Long productId, int quantity);
    InventoryResponse releaseReservedStock(Long productId, int quantity);
    InventoryResponse updateReorderLevel(Long productId, int reorderLevel, Integer maxStockLevel);
    InventoryResponse updateLocation(Long productId, String location);
    InventoryResponse recordStockCount(Long productId, int countedQuantity);
    Page<InventoryResponse> getLowStockAlerts(int page, int size);
    Page<InventoryResponse> getOutOfStockAlerts(int page, int size);
    Page<InventoryResponse> getAllInventory(int page, int size, String sortBy, String sortDir);
    Page<InventoryResponse> getByLocation(String location, int page, int size);
    InventorySummaryResponse getInventorySummary();
}