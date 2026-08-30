package com.shopsmart.repository;

import com.shopsmart.entity.Order;
import com.shopsmart.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        WHERE o.customer.id = :customerId
        AND o.status = :status
        ORDER BY o.createdAt DESC
    """)
    Page<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId,
                                           @Param("status") OrderStatus status,
                                           Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        WHERE o.createdAt BETWEEN :startDate AND :endDate
        ORDER BY o.createdAt DESC
    """)
    Page<Order> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        WHERE o.customer.id = :customerId
        AND o.createdAt BETWEEN :startDate AND :endDate
        ORDER BY o.createdAt DESC
    """)
    Page<Order> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        WHERE o.orderNumber LIKE CONCAT('%', :keyword, '%')
        OR o.customer.name LIKE CONCAT('%', :keyword, '%')
        OR o.customer.phone LIKE CONCAT('%', :keyword, '%')
        ORDER BY o.createdAt DESC
    """)
    Page<Order> search(@Param("keyword") String keyword, Pageable pageable);

    Long countByStatus(OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByDateRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT COUNT(o) FROM Order o
        WHERE o.createdAt BETWEEN :startDate AND :endDate
        AND o.status = :status
    """)
    Long countByStatusAndDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("status") OrderStatus status);
}