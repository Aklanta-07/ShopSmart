package com.shopsmart.repository;

import com.shopsmart.entity.Order;
import com.shopsmart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
        SELECT oi FROM OrderItem oi
        WHERE oi.order.id = :orderId
        ORDER BY oi.createdAt ASC
    """)
    List<OrderItem> findByOrderIdOrdered(@Param("orderId") Long orderId);
}