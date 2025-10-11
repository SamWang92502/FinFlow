package com.sam.finflow.domain.customer;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class CustomerService {

    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    private static String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase();
    }

    @Transactional
    public Customer createCustomer(String email, String fullName) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("fullName required");

        String normalized = normalizeEmail(email);

        // Prefer an exists-check that matches your DB semantics
        // (implement existsByEmailIgnoreCase in the repository if you like)
        customers.findByEmail(normalized).ifPresent(c -> {
            throw new IllegalStateException("email already exists: " + normalized);
        });

        Customer c = new Customer();
        c.setEmail(normalized);
        c.setFullName(fullName.trim());

        try {
            return customers.save(c);
        } catch (DataIntegrityViolationException dup) {
            // Handles concurrent creates that hit the unique constraint
            throw new IllegalStateException("email already exists: " + normalized, dup);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getCustomer(UUID id) {
        return customers.findById(id);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerOrThrow(UUID id) {
        return customers.findById(id)
                .orElseThrow(() -> new NoSuchElementException("customer not found: " + id));
    }
}
