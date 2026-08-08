package com.shopsmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsmart.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByIsActiveTrue();
    List<Category> findByIsActiveTrueOrderByNameAsc();
    Page<Category> findByIsActiveTrue(Pageable pageable);
    Page<Category> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
}