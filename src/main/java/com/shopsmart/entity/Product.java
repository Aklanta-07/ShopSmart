package com.shopsmart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCTS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "sku"),
        @UniqueConstraint(columnNames = "barcode")
    },
    indexes = {
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_sku", columnList = "sku"),
        @Index(name = "idx_product_barcode", columnList = "barcode"),
        @Index(name = "idx_product_active", columnList = "is_active")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @SequenceGenerator(name = "product_seq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(unique = true, length = 100)
    private String barcode;

    @Column(columnDefinition = "CLOB")
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal costPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductUnit unit = ProductUnit.PIECE;

    @Column(name = "reorder_level", nullable = false)
    @Builder.Default
    private Integer reorderLevel = 10;

    @Column(name = "max_stock_level")
    private Integer maxStockLevel;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Inventory inventory;
}