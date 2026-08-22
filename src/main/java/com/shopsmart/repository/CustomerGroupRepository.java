package com.shopsmart.repository;

import com.shopsmart.entity.CustomerGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Long> {

    Optional<CustomerGroup> findByName(String name);

    List<CustomerGroup> findByIsActiveTrue();

    Page<CustomerGroup> findByIsActiveTrue(Pageable pageable);

    @Query("""
        SELECT cg FROM CustomerGroup cg
        WHERE cg.isActive = true
        AND LOWER(cg.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY cg.name ASC
    """)
    Page<CustomerGroup> search(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByName(String name);
}