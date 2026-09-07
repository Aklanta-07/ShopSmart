    package com.shopsmart.service;

import com.shopsmart.dto.request.CreateOrderRequest;
import com.shopsmart.dto.request.OrderSearchRequest;
import com.shopsmart.dto.request.PaymentRequest;
import com.shopsmart.dto.response.CustomerSummaryResponse;
import com.shopsmart.dto.response.OrderItemResponse;
import com.shopsmart.dto.response.OrderResponse;
import com.shopsmart.dto.response.OrderSummaryResponse;
import com.shopsmart.dto.response.PaymentResponse;
import com.shopsmart.dto.response.RecentOrderResponse;
import com.shopsmart.entity.Customer;
import com.shopsmart.entity.Order;
import com.shopsmart.entity.OrderItem;
import com.shopsmart.entity.OrderStatus;
import com.shopsmart.entity.Payment;
import com.shopsmart.entity.PaymentMethod;
import com.shopsmart.entity.Product;
import com.shopsmart.exception.CustomerNotFoundException;
import com.shopsmart.exception.InsufficientStockException;
import com.shopsmart.exception.InvalidOrderStatusException;
import com.shopsmart.exception.OrderCannotBeCancelledException;
import com.shopsmart.exception.OrderCannotBeModifiedException;
import com.shopsmart.exception.OrderNotFoundException;
import com.shopsmart.exception.ProductNotFoundException;
import com.shopsmart.repository.CustomerRepository;
import com.shopsmart.repository.OrderItemRepository;
import com.shopsmart.repository.OrderRepository;
import com.shopsmart.repository.PaymentRepository;
import com.shopsmart.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
        if (!customer.getIsActive()) {
            throw new IllegalArgumentException("Customer is not active");
        }

        // 2. Validate and reserve stock for each item
        for (CreateOrderRequest.CreateOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemReq.getProductId()));
            if (!product.getIsActive()) {
                throw new IllegalArgumentException("Product " + product.getName() + " is not active");
            }

            // Check and reserve stock
            try {
                inventoryService.reserveStock(itemReq.getProductId(), itemReq.getQuantity());
            } catch (InsufficientStockException e) {
                throw new InsufficientStockException(
                        itemReq.getProductId(),
                        itemReq.getQuantity(),
                        inventoryService.getByProductId(itemReq.getProductId()).getQuantityAvailable()
                );
            }
        }

        // 3. Calculate pricing with customer group discount
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (CreateOrderRequest.CreateOrderItemRequest itemReq : request.getItems()) {
            BigDecimal basePrice = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal discount = basePrice.multiply(itemReq.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal afterDiscount = basePrice.subtract(discount);
            BigDecimal tax = afterDiscount.multiply(itemReq.getTaxRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(basePrice);
            totalDiscount = totalDiscount.add(discount);
            totalTax = totalTax.add(tax);
        }

        // Apply customer group discount if applicable
        if (customer.getGroup() != null && customer.getGroup().getDiscountPercentage() != null) {
            BigDecimal groupDiscount = subtotal.multiply(customer.getGroup().getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalDiscount = totalDiscount.add(groupDiscount);
        }

        // Add order-level discount if provided
        if (request.getDiscountAmount() != null) {
            totalDiscount = totalDiscount.add(request.getDiscountAmount());
        }

        BigDecimal totalAmount = subtotal.subtract(totalDiscount).add(totalTax);

        // 4. Create order
        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .discountAmount(totalDiscount)
                .taxAmount(totalTax)
                .totalAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .balanceDue(totalAmount)
                .notes(request.getNotes())
                .build();

        // 5. Create order items
        for (CreateOrderRequest.CreateOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).get();
            BigDecimal basePrice = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal discount = basePrice.multiply(itemReq.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal afterDiscount = basePrice.subtract(discount);
            BigDecimal tax = afterDiscount.multiply(itemReq.getTaxRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = afterDiscount.add(tax);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .discountPercentage(itemReq.getDiscountPercentage())
                    .discountAmount(discount)
                    .lineTotal(lineTotal)
                    .taxRate(itemReq.getTaxRate())
                    .taxAmount(tax)
                    .build();

            order.getItems().add(item);
        }

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> search(OrderSearchRequest request, Pageable pageable) {
        Page<Order> orders;

        if (request.getCustomerId() != null && request.getStatus() != null) {
            orders = orderRepository.findByCustomerIdAndStatus(request.getCustomerId(), request.getStatus(), pageable);
        } else if (request.getCustomerId() != null) {
            orders = orderRepository.findByCustomerId(request.getCustomerId(), pageable);
        } else if (request.getStatus() != null) {
            orders = orderRepository.findByStatus(request.getStatus(), pageable);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            orders = orderRepository.findByDateRange(request.getStartDate(), request.getEndDate(), pageable);
        } else if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            orders = orderRepository.search(request.getKeyword(), pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::toResponse);
    }   

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.canBeModified()) {
            throw new OrderCannotBeModifiedException(order.getOrderNumber(), order.getStatus().name());
        }

        order.setStatus(OrderStatus.CONFIRMED);
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse processOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException(order.getStatus().name(), OrderStatus.CONFIRMED.name());
        }

        order.setStatus(OrderStatus.PROCESSING);
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() != OrderStatus.PROCESSING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException(order.getStatus().name(), "PROCESSING or CONFIRMED");
        }

        if (!order.isFullyPaid()) {
            throw new IllegalStateException("Order is not fully paid. Balance due: " + order.getBalanceDue());
        }

        // Confirm reserved stock (stock was reserved, now sale is final - reduce on-hand)
        for (OrderItem item : order.getItems()) {
            try {
                inventoryService.confirmReservedStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                // Log but don't fail - stock might already be confirmed
            }
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.canBeCancelled()) {
            throw new OrderCannotBeCancelledException(order.getOrderNumber(), order.getStatus().name());
        }

        // Release reserved stock
        for (OrderItem item : order.getItems()) {
            try {
                inventoryService.releaseReservedStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                // Log but don't fail
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse addPayment(Long orderId, PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new InvalidOrderStatusException(order.getStatus().name(), "active order");
        }

        if (request.getAmount().compareTo(order.getBalanceDue()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds balance due: " + order.getBalanceDue());
        }

        // Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .method(request.getMethod())
                .referenceNumber(request.getReferenceNumber())
                .receivedBy(request.getReceivedBy())
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Update order paid amount
        BigDecimal newPaidAmount = order.getPaidAmount().add(request.getAmount());
        order.setPaidAmount(newPaidAmount);
        order.setBalanceDue(order.getTotalAmount().subtract(newPaidAmount));

        // Auto-complete if fully paid and in processing/confirmed status
        if (order.isFullyPaid() && (order.getStatus() == OrderStatus.PROCESSING || order.getStatus() == OrderStatus.CONFIRMED)) {
            // Confirm reserved stock (reduce on-hand as items are sold)
            for (OrderItem item : order.getItems()) {
                try {
                    inventoryService.confirmReservedStock(item.getProductId(), item.getQuantity());
                } catch (Exception e) {
                    // Log but don't fail
                }
            }
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
        } else if (order.isFullyPaid()) {
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
        }

        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getOrderSummary() {
        return getOrderSummary(LocalDateTime.now().minusDays(30), LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getOrderSummary(LocalDateTime startDate, LocalDateTime endDate) {
        Long totalOrders = orderRepository.count();
        Long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        Long confirmedOrders = orderRepository.countByStatus(OrderStatus.CONFIRMED);
        Long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
        Long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        BigDecimal totalRevenue = orderRepository.sumTotalAmountByDateRange(startDate, endDate);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // Recent orders
        Pageable recentPageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<RecentOrderResponse> recentOrders = orderRepository.findAll(recentPageable).getContent().stream()
                .map(this::toRecentOrderResponse)
                .collect(Collectors.toList());

        // Revenue by payment method
        List<Object[]> paymentStats = paymentRepository.sumByMethodAndDateRange(startDate, endDate);
        Map<PaymentMethod, BigDecimal> revenueByMethod = paymentStats.stream()
                .collect(Collectors.toMap(
                        arr -> (PaymentMethod) arr[0],
                        arr -> (BigDecimal) arr[1]
                ));

        // Orders by status
        List<OrderStatus> statuses = List.of(OrderStatus.values());
        Map<OrderStatus, Long> ordersByStatus = statuses.stream()
                .collect(Collectors.toMap(
                        status -> status,
                        status -> orderRepository.countByStatus(status)
                ));

        // Pending amount
        BigDecimal pendingAmount = orderRepository.findByStatus(OrderStatus.PENDING).stream()
                .map(Order::getBalanceDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderSummaryResponse.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .totalPendingAmount(pendingAmount)
                .recentOrders(recentOrders)
                .revenueByPaymentMethod(revenueByMethod)
                .ordersByStatus(ordersByStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = orderRepository.sumTotalAmountByDateRange(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public OrderResponse updateOrderNotes(Long id, String notes) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.canBeModified()) {
            throw new OrderCannotBeModifiedException(order.getOrderNumber(), order.getStatus().name());
        }

        order.setNotes(notes);
        return toResponse(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private OrderResponse toResponse(Order order) {
        CustomerSummaryResponse customerResponse = CustomerSummaryResponse.builder()
                .id(order.getCustomer().getId())
                .name(order.getCustomer().getName())
                .phone(order.getCustomer().getPhone())
                .email(order.getCustomer().getEmail())
                .type(order.getCustomer().getType())
                .creditLimit(order.getCustomer().getCreditLimit())
                .availableCredit(order.getCustomer().getAvailableCredit())
                .loyaltyPoints(order.getCustomer().getLoyaltyPoints())
                .build();

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discountPercentage(item.getDiscountPercentage())
                        .discountAmount(item.getDiscountAmount())
                        .lineTotal(item.getLineTotal())
                        .taxRate(item.getTaxRate())
                        .taxAmount(item.getTaxAmount())
                        .build())
                .collect(Collectors.toList());

        List<PaymentResponse> paymentResponses = order.getPayments().stream()
                .map(payment -> PaymentResponse.builder()
                        .id(payment.getId())
                        .orderId(payment.getOrder().getId())
                        .amount(payment.getAmount())
                        .method(payment.getMethod())
                        .referenceNumber(payment.getReferenceNumber())
                        .receivedBy(payment.getReceivedBy())
                        .paidAt(payment.getPaidAt())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customer(customerResponse)
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .paidAmount(order.getPaidAmount())
                .balanceDue(order.getBalanceDue())
                .notes(order.getNotes())
                .completedAt(order.getCompletedAt())
                .items(itemResponses)
                .payments(paymentResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private RecentOrderResponse toRecentOrderResponse(Order order) {
        PaymentMethod lastPaymentMethod = order.getPayments().isEmpty() ? null :
                order.getPayments().get(order.getPayments().size() - 1).getMethod();

        return RecentOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomer().getName())
                .customerPhone(order.getCustomer().getPhone())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .paidAmount(order.getPaidAmount())
                .balanceDue(order.getBalanceDue())
                .paymentMethod(lastPaymentMethod)
                .createdAt(order.getCreatedAt())
                .build();
    }
}