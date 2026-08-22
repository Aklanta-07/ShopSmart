package com.shopsmart.repository;

import com.shopsmart.entity.Customer;
import com.shopsmart.entity.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByType(CustomerType type);

    List<Customer> findByIsActiveTrue();

    Page<Customer> findByIsActiveTrue(Pageable pageable);

    @Query("""
        SELECT c FROM Customer c
        WHERE c.isActive = true
        AND (
            LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR c.phone LIKE CONCAT('%', :keyword, '%')
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY c.createdAt DESC
    """)
    Page<Customer> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT c FROM Customer c
        WHERE c.isActive = true
        AND c.group.id = :groupId
        ORDER BY c.createdAt DESC
    """)
    Page<Customer> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    Long countByIsActiveTrue();

    Long countByType(CustomerType type);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}