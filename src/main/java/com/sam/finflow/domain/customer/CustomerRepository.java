package com.sam.finflow.domain.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional; // ← you need this


@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
     Optional<Customer> findByEmail(String email);
}
