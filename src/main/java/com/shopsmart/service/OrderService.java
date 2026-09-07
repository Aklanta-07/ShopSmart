package com.shopsmart.service;

import com.shopsmart.dto.request.CreateOrderRequest;
import com.shopsmart.dto.request.OrderSearchRequest;
import com.shopsmart.dto.request.PaymentRequest;
import com.shopsmart.dto.response.OrderResponse;
import com.shopsmart.dto.response.OrderSummaryResponse;
import com.shopsmart.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getById(Long id);

    OrderResponse getByOrderNumber(String orderNumber);

    Page<OrderResponse> search(OrderSearchRequest request, Pageable pageable);

    List<OrderResponse> getByCustomerId(Long customerId);

    List<OrderResponse> getByStatus(OrderStatus status);

    OrderResponse confirmOrder(Long id);

    OrderResponse processOrder(Long id);

    OrderResponse completeOrder(Long id);

    OrderResponse cancelOrder(Long id);

    OrderResponse addPayment(Long orderId, PaymentRequest request);

    OrderSummaryResponse getOrderSummary();

    OrderSummaryResponse getOrderSummary(LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

    Long getOrderCountByStatus(OrderStatus status);

    OrderResponse updateOrderNotes(Long id, String notes);
}