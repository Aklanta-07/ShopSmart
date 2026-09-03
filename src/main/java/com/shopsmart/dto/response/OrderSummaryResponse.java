package com.shopsmart.dto.response;

import com.shopsmart.entity.OrderStatus;
import com.shopsmart.entity.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long totalOrders;
    private Long pendingOrders;
    private Long confirmedOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal totalPendingAmount;
    private List<RecentOrderResponse> recentOrders;
    private Map<PaymentMethod, BigDecimal> revenueByPaymentMethod;
    private Map<OrderStatus, Long> ordersByStatus;
}