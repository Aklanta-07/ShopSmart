package com.shopsmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopsmart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    Optional<Product> findByBarcode(String barcode);
    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);

    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    List<Product> findByIsActiveTrue();
    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
    Page<Product> findBySkuContainingIgnoreCaseAndIsActiveTrue(String sku, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.name ILIKE %:search% OR p.sku ILIKE %:search% OR p.barcode ILIKE %:search%)")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);

    List<Product> findByInventoryQuantityAvailableLessThanEqual(int threshold);
}