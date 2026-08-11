package com.shopsmart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsmart.dto.request.ProductRequest;
import com.shopsmart.dto.request.StockAdjustmentRequest;
import com.shopsmart.dto.response.InventorySummary;
import com.shopsmart.dto.response.ProductResponse;
import com.shopsmart.entity.Category;
import com.shopsmart.entity.Inventory;
import com.shopsmart.entity.Product;
import com.shopsmart.entity.ProductUnit;
import com.shopsmart.exception.CategoryNotFoundException;
import com.shopsmart.exception.DuplicateSkuException;
import com.shopsmart.exception.ProductNotFoundException;
import com.shopsmart.repository.CategoryRepository;
import com.shopsmart.repository.InventoryRepository;
import com.shopsmart.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    @Override
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateSkuException("Barcode " + request.getBarcode() + " already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.ZERO)
                .unit(request.getUnit() != null ? request.getUnit() : ProductUnit.PIECE)
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 10)
                .maxStockLevel(request.getMaxStockLevel())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .category(category)
                .build();

        Product saved = productRepository.save(product);

        // Create inventory record
        Inventory inventory = Inventory.builder()
                .product(saved)
                .quantityOnHand(0)
                .quantityReserved(0)
                .quantityAvailable(0)
                .reorderLevel(saved.getReorderLevel())
                .maxStockLevel(saved.getMaxStockLevel())
                .build();
        inventory.recalculateAvailable();
        inventoryRepository.save(inventory);

        return toResponse(saved);
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    @Override
    public ProductResponse getBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("sku", sku));
        return toResponse(product);
    }

    @Override
    public ProductResponse getByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("barcode", barcode));
        return toResponse(product);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }
        if (request.getBarcode() != null && !request.getBarcode().equals(product.getBarcode())
            && productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateSkuException("Barcode " + request.getBarcode() + " already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setTaxRate(request.getTaxRate() != null ? request.getTaxRate() : product.getTaxRate());
        product.setUnit(request.getUnit() != null ? request.getUnit() : product.getUnit());
        product.setReorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : product.getReorderLevel());
        product.setMaxStockLevel(request.getMaxStockLevel());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : product.getIsActive());
        product.setCategory(category);

        // Update inventory reorder level if changed
        if (product.getInventory() != null) {
            product.getInventory().setReorderLevel(product.getReorderLevel());
            product.getInventory().setMaxStockLevel(product.getMaxStockLevel());
        }

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> getAll(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Page<ProductResponse> getByCategory(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable).map(this::toResponse);
    }

    @Override
    public Page<ProductResponse> search(String query, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        return productRepository.searchProducts(query, pageable).map(this::toResponse);
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findByInventoryQuantityAvailableLessThanEqual(10).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateStock(Long productId, StockAdjustmentRequest request) {
        // Delegate to inventory service for stock operations
        inventoryService.adjustStock(productId, request);
        return getById(productId);
    }

    private ProductResponse toResponse(Product product) {
        InventorySummary inventorySummary = null;
        if (product.getInventory() != null) {
            Inventory inv = product.getInventory();
            inventorySummary = InventorySummary.builder()
                    .quantityOnHand(inv.getQuantityOnHand())
                    .quantityReserved(inv.getQuantityReserved())
                    .quantityAvailable(inv.getQuantityAvailable())
                    .reorderLevel(inv.getReorderLevel())
                    .maxStockLevel(inv.getMaxStockLevel())
                    .location(inv.getLocation())
                    .isLowStock(inv.isLowStock())
                    .isOutOfStock(inv.isOutOfStock())
                    .lastRestockedAt(inv.getLastRestockedAt())
                    .lastCountedAt(inv.getLastCountedAt())
                    .build();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .description(product.getDescription())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .taxRate(product.getTaxRate())
                .unit(product.getUnit())
                .reorderLevel(product.getReorderLevel())
                .maxStockLevel(product.getMaxStockLevel())
                .isActive(product.getIsActive())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .inventory(inventorySummary)
                .build();
    }
}