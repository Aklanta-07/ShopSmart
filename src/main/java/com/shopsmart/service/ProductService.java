package com.shopsmart.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.shopsmart.dto.request.ProductRequest;
import com.shopsmart.dto.request.StockAdjustmentRequest;
import com.shopsmart.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse getById(Long id);
    ProductResponse getBySku(String sku);
    ProductResponse getByBarcode(String barcode);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    Page<ProductResponse> getAll(int page, int size, String sortBy, String sortDir);
    Page<ProductResponse> getByCategory(Long categoryId, int page, int size, String sortBy, String sortDir);
    Page<ProductResponse> search(String query, int page, int size, String sortBy, String sortDir);
    List<ProductResponse> getActiveProducts();
    List<ProductResponse> getLowStockProducts();
    ProductResponse updateStock(Long productId, StockAdjustmentRequest request);
}