package com.shopsmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopsmart.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
    boolean existsByProductId(Long productId);

    List<Inventory> findByQuantityAvailableLessThanEqual(int threshold);
    List<Inventory> findByQuantityAvailableLessThanEqualAndProductIsActiveTrue(int threshold);
    Page<Inventory> findByQuantityAvailableLessThanEqualAndProductIsActiveTrue(int threshold, Pageable pageable);

    List<Inventory> findByLocation(String location);
    Page<Inventory> findByLocation(String location, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable <= 0 AND i.product.isActive = true")
    List<Inventory> findOutOfStockProducts();

    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable <= i.reorderLevel AND i.quantityAvailable > 0 AND i.product.isActive = true")
    List<Inventory> findLowStockProducts();

    @Query("SELECT SUM(i.quantityOnHand) FROM Inventory i WHERE i.product.isActive = true")
    Long getTotalStockCount();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantityAvailable <= i.reorderLevel AND i.product.isActive = true")
    Long getLowStockCount();
}