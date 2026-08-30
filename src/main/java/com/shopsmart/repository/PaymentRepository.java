package com.shopsmart.repository;

import com.shopsmart.entity.Order;
import com.shopsmart.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrder(Order order);

    List<Payment> findByOrderId(Long orderId);

    Page<Payment> findByOrderId(Long orderId, Pageable pageable);

    @Query("""
        SELECT p FROM Payment p
        WHERE p.paidAt BETWEEN :startDate AND :endDate
        ORDER BY p.paidAt DESC
    """)
    Page<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    @Query("""
        SELECT SUM(p.amount) FROM Payment p
        WHERE p.paidAt BETWEEN :startDate AND :endDate
    """)
    BigDecimal sumAmountByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("""
        SELECT p.method, SUM(p.amount) FROM Payment p
        WHERE p.paidAt BETWEEN :startDate AND :endDate
        GROUP BY p.method
    """)
    List<Object[]> sumByMethodAndDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
}