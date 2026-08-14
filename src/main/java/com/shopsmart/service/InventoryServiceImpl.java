package com.shopsmart.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsmart.dto.request.StockAdjustmentRequest;
import com.shopsmart.dto.request.AdjustmentType;
import com.shopsmart.dto.response.InventoryResponse;
import com.shopsmart.dto.response.InventorySummaryResponse;
import com.shopsmart.dto.response.LowStockAlertResponse;
import com.shopsmart.entity.Inventory;
import com.shopsmart.entity.Product;
import com.shopsmart.exception.InsufficientStockException;
import com.shopsmart.exception.InventoryNotFoundException;
import com.shopsmart.exception.InvalidStockOperationException;
import com.shopsmart.repository.InventoryRepository;
import com.shopsmart.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
        return toResponse(inventory);
    }

    @Override
    public InventoryResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        int qty = request.getQuantity();
        switch (request.getType()) {
            case INCREASE:
            case RESTOCK:
            case RETURN:
            case TRANSFER_IN:
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + qty);
                if (request.getType() == AdjustmentType.RESTOCK) {
                    inventory.setLastRestockedAt(LocalDateTime.now());
                }
                break;
            case DECREASE:
            case DAMAGE:
            case TRANSFER_OUT:
                if (inventory.getQuantityAvailable() < qty) {
                    throw new InsufficientStockException(productId, qty, inventory.getQuantityAvailable());
                }
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() - qty);
                break;
            case SET:
                if (qty < 0) {
                    throw new InvalidStockOperationException("Quantity cannot be negative for SET operation");
                }
                inventory.setQuantityOnHand(qty);
                inventory.setLastCountedAt(LocalDateTime.now());
                break;
            default:
                throw new InvalidStockOperationException("Unknown adjustment type: " + request.getType());
        }
        inventory.recalculateAvailable();
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    @Override
    public InventoryResponse restock(Long productId, int quantity, String location) {
        if (quantity <= 0) {
            throw new InvalidStockOperationException("Restock quantity must be positive");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
        inventory.setLastRestockedAt(LocalDateTime.now());
        if (location != null && !location.isBlank()) {
            inventory.setLocation(location);
        }
        inventory.recalculateAvailable();
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse reserveStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockOperationException("Reserve quantity must be positive");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        if (inventory.getQuantityAvailable() < quantity) {
            throw new InsufficientStockException(productId, quantity, inventory.getQuantityAvailable());
        }
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        inventory.recalculateAvailable();
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse releaseReservedStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockOperationException("Release quantity must be positive");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        if (inventory.getQuantityReserved() < quantity) {
            throw new InvalidStockOperationException("Cannot release more than reserved quantity");
        }
        inventory.setQuantityReserved(inventory.getQuantityReserved() - quantity);
        inventory.recalculateAvailable();
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse updateReorderLevel(Long productId, int reorderLevel, Integer maxStockLevel) {
        if (reorderLevel < 0) {
            throw new InvalidStockOperationException("Reorder level cannot be negative");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.setReorderLevel(reorderLevel);
        if (maxStockLevel != null) {
            if (maxStockLevel < 0) {
                throw new InvalidStockOperationException("Max stock level cannot be negative");
            }
            inventory.setMaxStockLevel(maxStockLevel);
        }
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse updateLocation(Long productId, String location) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.setLocation(location);
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryResponse recordStockCount(Long productId, int countedQuantity) {
        if (countedQuantity < 0) {
            throw new InvalidStockOperationException("Counted quantity cannot be negative");
        }
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.setQuantityOnHand(countedQuantity);
        inventory.setLastCountedAt(LocalDateTime.now());
        inventory.recalculateAvailable();
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public Page<InventoryResponse> getLowStockAlerts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("quantityAvailable").ascending());
        return inventoryRepository.findByQuantityAvailableLessThanEqualAndProductIsActiveTrue(10, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<InventoryResponse> getOutOfStockAlerts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("quantityAvailable").ascending());
        return inventoryRepository.findByQuantityAvailableLessThanEqualAndProductIsActiveTrue(0, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<InventoryResponse> getAllInventory(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        return inventoryRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Page<InventoryResponse> getByLocation(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepository.findByLocation(location, pageable).map(this::toResponse);
    }

    @Override
    public InventorySummaryResponse getInventorySummary() {
        Long totalProducts = productRepository.countByIsActiveTrue();
        Long totalStockItems = inventoryRepository.getTotalStockCount();
        Long lowStockCount = inventoryRepository.getLowStockCount();
        Long outOfStockCount = (long) inventoryRepository.findOutOfStockProducts().size();

        BigDecimal totalStockValue = inventoryRepository.findAll().stream()
                .filter(i -> i.getProduct().getIsActive())
                .map(i -> i.getProduct().getCostPrice().multiply(BigDecimal.valueOf(i.getQuantityAvailable())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<LowStockAlertResponse> topLowStockItems = inventoryRepository.findLowStockProducts().stream()
                .limit(10)
                .map(this::toLowStockAlert)
                .collect(Collectors.toList());

        return InventorySummaryResponse.builder()
                .totalProducts(totalProducts)
                .totalStockItems(totalStockItems != null ? totalStockItems : 0L)
                .lowStockCount(lowStockCount != null ? lowStockCount : 0L)
                .outOfStockCount(outOfStockCount)
                .totalStockValue(totalStockValue)
                .topLowStockItems(topLowStockItems)
                .build();
    }

    private InventoryResponse toResponse(Inventory inventory) {
        Product product = inventory.getProduct();
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSku(product.getSku())
                .categoryName(product.getCategory().getName())
                .quantityOnHand(inventory.getQuantityOnHand())
                .quantityReserved(inventory.getQuantityReserved())
                .quantityAvailable(inventory.getQuantityAvailable())
                .reorderLevel(inventory.getReorderLevel())
                .maxStockLevel(inventory.getMaxStockLevel())
                .location(inventory.getLocation())
                .isLowStock(inventory.isLowStock())
                .isOutOfStock(inventory.isOutOfStock())
                .lastRestockedAt(inventory.getLastRestockedAt())
                .lastCountedAt(inventory.getLastCountedAt())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private LowStockAlertResponse toLowStockAlert(Inventory inventory) {
        Product product = inventory.getProduct();
        int shortage = inventory.getReorderLevel() - inventory.getQuantityAvailable();
        return LowStockAlertResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .categoryName(product.getCategory().getName())
                .quantityAvailable(inventory.getQuantityAvailable())
                .reorderLevel(inventory.getReorderLevel())
                .shortage(shortage)
                .location(inventory.getLocation())
                .lastRestockedAt(inventory.getLastRestockedAt())
                .build();
    }
}