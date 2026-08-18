package com.shopsmart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_group")
@SequenceGenerator(name = "customer_group_seq", sequenceName = "customer_group_seq", allocationSize = 1)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_group_seq")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}