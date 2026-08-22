package com.shopsmart.repository;

import com.shopsmart.entity.Customer;
import com.shopsmart.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomerAndIsActiveTrue(Customer customer);

    Optional<CustomerAddress> findByCustomerAndIsDefaultTrueAndIsActiveTrue(Customer customer);

    @Query("""
        SELECT ca FROM CustomerAddress ca
        WHERE ca.customer = :customer
        AND ca.type = :type
        AND ca.isActive = true
    """)
    Optional<CustomerAddress> findByCustomerAndType(@Param("customer") Customer customer, @Param("type") String type);
}