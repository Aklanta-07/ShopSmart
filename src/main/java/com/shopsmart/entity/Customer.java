package com.shopsmart.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
@SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq", allocationSize = 1)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private CustomerGroup group;

    private Integer loyaltyPoints;

    @Column(precision = 12, scale = 2)
    private BigDecimal creditLimit;

    @Column(precision = 12, scale = 2)
    private BigDecimal creditUsed;

    @Column(length = 20)
    private String gstNumber;

    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustomerAddress> addresses = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public BigDecimal getAvailableCredit() {
        if (creditLimit == null) return BigDecimal.ZERO;
        BigDecimal used = creditUsed != null ? creditUsed : BigDecimal.ZERO;
        return creditLimit.subtract(used);
    }

    public boolean hasCreditAvailable(BigDecimal amount) {
        return getAvailableCredit().compareTo(amount) >= 0;
    }
}