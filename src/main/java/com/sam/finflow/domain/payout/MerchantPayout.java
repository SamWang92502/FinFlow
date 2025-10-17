package com.sam.finflow.domain.payout;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "merchant_payouts",
        uniqueConstraints = {
                // Prevent duplicate payouts for the same merchant capture
                @UniqueConstraint(name = "uq_payout_merchant_capture", columnNames = {"merchant_id", "capture_id"})
        },
        indexes = {
                @Index(name = "idx_payout_merchant", columnList = "merchant_id"),
                @Index(name = "idx_payout_status_created", columnList = "status, created_at")
        }
)
public class MerchantPayout {

    //PENDING: created but not sent yet.
    //PROCESSING: Worker/connector starts sending to bank
    public enum Status { PENDING, PROCESSING, SETTLED, FAILED }

    @Id @GeneratedValue
    private UUID id;

    @NotNull
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @NotNull
    @Column(name = "merchant_settlement_account_id", nullable = false)
    private UUID merchantSettlementAccountId; // where money is sent

    /** Business id to ensure one payout per captured order (or use an idempotency key) */
    @NotBlank
    @Size(max = 64)
    @Column(name = "capture_id", nullable = false, length = 64)
    private String captureId; //the thing we’re paying for.

    @NotNull @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    //To prevent lost updates when two requests modify the same payout at the same time.
    @Version
    private long version;

    protected MerchantPayout() { /* for JPA */ }

    public MerchantPayout(UUID merchantId,
                          UUID merchantSettlementAccountId,
                          String captureId,
                          BigDecimal amount,
                          String currency) {

        if (merchantId == null) throw new IllegalArgumentException("merchantId required");
        if (merchantSettlementAccountId == null) throw new IllegalArgumentException("merchantSettlementAccountId required");
        if (captureId == null || captureId.isBlank()) throw new IllegalArgumentException("captureId required");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");

        this.merchantId = merchantId;
        this.merchantSettlementAccountId = merchantSettlementAccountId;
        this.captureId = captureId.trim();
        this.amount = amount.setScale(2);
        this.currency = (currency == null || currency.isBlank()) ? "USD" : currency.trim().toUpperCase();
        if (this.currency.length() != 3) throw new IllegalArgumentException("currency must be 3 letters");
    }

    // ----- Domain behavior (guarded transitions) -----
    public void markProcessing() {
        if (status != Status.PENDING) throw new IllegalStateException("Only PENDING → PROCESSING");
        status = Status.PROCESSING;
    }

    public void markSettled() {
        if (status != Status.PROCESSING) throw new IllegalStateException("Only PROCESSING → SETTLED");
        status = Status.SETTLED;
    }

    public void markFailed() {
        if (status == Status.SETTLED || status == Status.FAILED) return; // idempotent no-op
        if (status != Status.PENDING && status != Status.PROCESSING)
            throw new IllegalStateException("Fail only from PENDING/PROCESSING");
        status = Status.FAILED;
    }

    // ----- Getters (no public setters) -----
    public UUID getId() { return id; }
    public UUID getMerchantId() { return merchantId; }
    public UUID getMerchantSettlementAccountId() { return merchantSettlementAccountId; }
    public String getCaptureId() { return captureId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Status getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }

    //@Override: I’m replacing a method that already exists in a parent class
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MerchantPayout)) return false;
        MerchantPayout that = (MerchantPayout) o;
        return id != null && id.equals(that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
    @Override public String toString() {
        return "MerchantPayout{id=%s, merchantId=%s, captureId=%s, amount=%s %s, status=%s}"
                .formatted(id, merchantId, captureId, amount, currency, status);
    }
}
