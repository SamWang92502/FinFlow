package com.sam.finflow.domain.disbursement;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "disbursements")
public class Disbursement {

    public enum Status { PENDING, PROCESSING, SETTLED, FAILED }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID bankLinkId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(unique = true)
    private String idempotencyKey; // to prevent double-sends

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected Disbursement() { }

    public Disbursement(UUID customerId, UUID bankLinkId, BigDecimal amount, String currency, String idempotencyKey) {
        this.customerId = customerId;
        this.bankLinkId = bankLinkId;
        this.amount = amount;
        if (currency != null) this.currency = currency;
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public UUID getBankLinkId() { return bankLinkId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Status getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getIdempotencyKey() { return idempotencyKey; }

    public void markProcessing() { this.status = Status.PROCESSING; }
    public void markSettled() { this.status = Status.SETTLED; }
    public void markFailed() { this.status = Status.FAILED; }
}
