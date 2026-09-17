package com.shopsmart.controller;

import com.shopsmart.dto.request.CreateOrderRequest;
import com.shopsmart.dto.request.PaymentRequest;
import com.shopsmart.dto.response.OrderResponse;
import com.shopsmart.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController using H2 test database with seeded data.
 * Run with: ./mvnw test -Dtest=OrderControllerIntegrationTest
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rolls back after each test
@Sql(scripts = "/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    // Seeded test data IDs (from data-h2.sql)
    private static final Long CUSTOMER_PRIYA_ID = 2L;      // Priya Patel - REGULAR, group=Regular Members (5%)
    private static final Long CUSTOMER_KRISHNA_ID = 3L;    // Krishna Traders - WHOLESALE, group=Wholesale Partners (15%)
    private static final Long PRODUCT_SAMSUNG_ID = 1L;     // Samsung Galaxy A54 - 36999, stock=45
    private static final Long PRODUCT_BOAT_ID = 2L;        // boAt Rockerz 450 - 1599, stock=80 (5 reserved)
    private static final Long PRODUCT_RICE_ID = 3L;        // Basmati Rice - 180, stock=200
    private static final Long PRODUCT_OIL_ID = 4L;         // Sunflower Oil - 165, stock=3 (LOW STOCK)
    private static final Long PRODUCT_JUICE_ID = 5L;       // Tropicana OJ - 120, stock=60

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // Required for @WithMockUser to take effect
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_withValidItems_returnsCreated() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(CUSTOMER_PRIYA_ID)
                .items(java.util.List.of(
                        CreateOrderRequest.CreateOrderItemRequest.builder()
                                .productId(PRODUCT_SAMSUNG_ID)
                                .quantity(1)
                                .unitPrice(new BigDecimal("36999.00"))
                                .discountPercentage(new BigDecimal("0.00"))
                                .taxRate(new BigDecimal("18.00"))
                                .build(),
                        CreateOrderRequest.CreateOrderItemRequest.builder()
                                .productId(PRODUCT_RICE_ID)
                                .quantity(2)
                                .unitPrice(new BigDecimal("180.00"))
                                .discountPercentage(new BigDecimal("5.00"))
                                .taxRate(new BigDecimal("5.00"))
                                .build()
                ))
                .notes("Test order from integration test")
                .discountAmount(new BigDecimal("0.00"))
                .taxAmount(new BigDecimal("6677.82"))
                .build();

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customer.id").value(CUSTOMER_PRIYA_ID.intValue()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("ORD-");
    }


    @Test
    @WithMockUser(roles = "STAFF")
    void createOrder_insufficientStock_returnsBadRequest() throws Exception {
        // Product OIL has only 3 in stock (LOW STOCK)
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(CUSTOMER_PRIYA_ID)
                .items(java.util.List.of(
                        CreateOrderRequest.CreateOrderItemRequest.builder()
                                .productId(PRODUCT_OIL_ID)
                                .quantity(10) // More than available (3)
                                .unitPrice(new BigDecimal("165.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fullOrderLifecycle_pendingToCompleted_works() throws Exception {
        // 1. Create order
        CreateOrderRequest createRequest = CreateOrderRequest.builder()
                .customerId(CUSTOMER_KRISHNA_ID)
                .items(java.util.List.of(
                        CreateOrderRequest.CreateOrderItemRequest.builder()
                                .productId(PRODUCT_BOAT_ID)
                                .quantity(2)
                                .unitPrice(new BigDecimal("1599.00"))
                                .discountPercentage(new BigDecimal("15.00")) // Wholesale 15% discount
                                .taxRate(new BigDecimal("18.00"))
                                .build()
                ))
                .discountAmount(new BigDecimal("0.00"))
                .taxAmount(new BigDecimal("575.64"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        OrderResponse created = asObject(createResult, OrderResponse.class);
        Long orderId = created.getId();

        // 2. Confirm order (reserves stock)
        mockMvc.perform(post("/api/orders/{id}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.items[0].productId").value(PRODUCT_BOAT_ID));

        // 3. Process order
        mockMvc.perform(post("/api/orders/{id}/process", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        // 4. Add payment — use order's actual total amount (service calculates its own)
        BigDecimal orderTotal = created.getTotalAmount();
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .amount(orderTotal) // Pay full amount
                .method(PaymentMethod.CARD)
                .referenceNumber("TXN-TEST-001")
                .receivedBy("Test Staff")
                .build();

        MvcResult paymentResult = mockMvc.perform(post("/api/orders/{id}/payment", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.paidAmount").value(created.getTotalAmount()))
                .andExpect(jsonPath("$.balanceDue").value(0))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrder_releasesReservedStock() throws Exception {
        // Create and confirm order first
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(CUSTOMER_PRIYA_ID)
                .items(java.util.List.of(
                        CreateOrderRequest.CreateOrderItemRequest.builder()
                                .productId(PRODUCT_JUICE_ID)
                                .quantity(5)
                                .unitPrice(new BigDecimal("120.00"))
                                .build()
                ))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        OrderResponse created = asObject(createResult, OrderResponse.class);
        Long orderId = created.getId();

        // Confirm (reserves 5 units from juice stock=60)
        mockMvc.perform(post("/api/orders/{id}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Cancel (should release reserved stock)
        mockMvc.perform(post("/api/orders/{id}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void searchOrders_withFilters_works() throws Exception {
        // Create a few orders first
        createTestOrder(CUSTOMER_PRIYA_ID, PRODUCT_RICE_ID, 1);
        createTestOrder(CUSTOMER_KRISHNA_ID, PRODUCT_JUICE_ID, 2);

        // Search by status PENDING
        mockMvc.perform(get("/api/orders")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));

        // Search by customer
        mockMvc.perform(get("/api/orders")
                        .param("customerId", CUSTOMER_PRIYA_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].customer.id").value(everyItem(equalTo(CUSTOMER_PRIYA_ID.intValue()))));

        // Search by keyword (order number pattern)
        mockMvc.perform(get("/api/orders")
                        .param("keyword", "ORD-"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderSummary_returnsDashboardData() throws Exception {
        createTestOrder(CUSTOMER_PRIYA_ID, PRODUCT_RICE_ID, 3);

        mockMvc.perform(get("/api/orders/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").exists())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.ordersByStatus").exists())
                .andExpect(jsonPath("$.recentOrders").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRevenue_forDateRange_works() throws Exception {
        mockMvc.perform(get("/api/orders/revenue")
                        .param("startDate", "2024-01-01T00:00:00")
                        .param("endDate", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void updateOrderNotes_works() throws Exception {
        MvcResult createResult = createTestOrder(CUSTOMER_PRIYA_ID, PRODUCT_RICE_ID, 1);
        OrderResponse created = asObject(createResult, OrderResponse.class);

        mockMvc.perform(patch("/api/orders/{id}/notes", created.getId())
                        .param("notes", "Updated via integration test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Updated via integration test"));
    }

    // ===== Helper methods =====

    private MvcResult createTestOrder(Long customerId, Long productId, int qty) throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(customerId)
                .items(java.util.List.of(
                        CreateOrderRequest.CreateOrderItemRequest.builder()
                                .productId(productId)
                                .quantity(qty)
                                .unitPrice(new BigDecimal("100.00"))
                                .build()
                ))
                .build();

        return mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String asJsonString(Object obj) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
    }

    private <T> T asObject(MvcResult result, Class<T> clazz) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(result.getResponse().getContentAsString(), clazz);
    }
}