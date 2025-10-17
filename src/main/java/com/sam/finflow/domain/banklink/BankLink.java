package com.sam.finflow.domain.banklink;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "bank_links",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cust_provider_account",
                        columnNames = {"customer_id", "provider", "provider_account_id"}
                )
        },
        indexes = {
                @Index(name = "idx_bank_links_customer", columnList = "customer_id"),
                @Index(name = "idx_bank_links_customer_status", columnList = "customer_id, status")
                // The partial unique index for one active method is created by SQL migration, not JPA
        }
)
public class BankLink {

    public enum Status { PENDING, ACTIVE, REVOKED, FAILED }

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId; //Model BankLink → single customerId: UUID

    @NotBlank
    @Column(nullable = false)
    private String provider; // 'plaid', 'teller', ...

    @NotBlank
    @Column(name = "provider_account_id", nullable = false)
    private String providerAccountId; // specific funding account id

    @Column(name = "institution_name")
    private String institutionName;

    @Size(min = 4, max = 4)
    @Column(length = 4)
    private String last4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    // 🆕 Add this new field — when customer gave consent
    @Column(name = "consent_at")
    private OffsetDateTime consentAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    protected BankLink() { }

    public BankLink(UUID customerId,
                    String provider,
                    String providerAccountId,
                    String institutionName,
                    String last4) {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (provider == null || provider.isBlank()) throw new IllegalArgumentException("provider required");
        if (providerAccountId == null || providerAccountId.isBlank()) throw new IllegalArgumentException("providerAccountId required");

        this.customerId = customerId;
        this.provider = provider.trim();
        this.providerAccountId = providerAccountId.trim();
        this.institutionName = institutionName;
        this.last4 = last4;
        this.status = Status.PENDING;
        this.primary = false;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getProvider() { return provider; }
    public String getProviderAccountId() { return providerAccountId; }
    public String getInstitutionName() { return institutionName; }
    public String getLast4() { return last4; }
    public Status getStatus() { return status; }
    public boolean isPrimary() { return primary; }
    public OffsetDateTime getConsentAt() { return consentAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getActivatedAt() { return activatedAt; }
    public OffsetDateTime getRevokedAt() { return revokedAt; }


    // Domain actions
    public void activate(OffsetDateTime consentAt) {
        if (status != Status.PENDING)
            throw new IllegalStateException("Only PENDING can activate");
        this.status = Status.ACTIVE;
        this.consentAt = (consentAt != null ? consentAt : OffsetDateTime.now()); // ✅ set here
        this.activatedAt = OffsetDateTime.now();
    }

    public void revoke() {
        if (status == Status.REVOKED) return;
        this.status = Status.REVOKED;
        this.revokedAt = OffsetDateTime.now();
        this.primary = false;
    }

    public void fail() {
        if (status == Status.REVOKED) return;
        this.status = Status.FAILED;
        this.primary = false;
    }

    public void makePrimary() {
        if (status != Status.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE links can be primary");
        }
        this.primary = true;
    }

    public void clearPrimary() {
        this.primary = false;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
