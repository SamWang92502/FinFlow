package com.sam.finflow.domain.customer;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue //Automatically generate the ID
    private UUID id; //Universally Unique Identifier

    @Column(nullable = false)
    @jakarta.validation.constraints.NotBlank
    private String fullName;

    @Column(nullable = false, unique = true)
    @jakarta.validation.constraints.Email
    private String email;

    @Column(nullable = false, updatable = false)
    private final OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    //updates the “Last Edited” timestamp
    @PreUpdate
    public void onUpdate() { updatedAt = OffsetDateTime.now(); }

    protected Customer() { } //For JPA

    public Customer(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email.trim().toLowerCase(); }
}
