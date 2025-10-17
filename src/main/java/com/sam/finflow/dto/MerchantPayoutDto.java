package com.sam.finflow.dto; // <-- adjust package if needed (e.g. com.sam.finflow.controller.dto)

import com.sam.finflow.domain.payout.MerchantPayout;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTOs (Data Transfer Objects) for MerchantPayout.
 * Used by controller to decouple HTTP payloads from domain entities.
 */
public final class MerchantPayoutDto {

    private MerchantPayoutDto() {
        // prevent instantiation
    }

    /**
     * Request DTO for creating a new Merchant Payout.
     */
    public record CreateRequest(
            @NotNull UUID merchantId,
            @NotNull UUID merchantSettlementAccountId,
            @NotBlank @Size(max = 64) String captureId,
            @NotNull @Positive BigDecimal amount,
            @NotBlank @Size(min = 3, max = 3) String currency
    ) {}

    /**
     * Response DTO for returning payout info to clients.
     */
    public record Response(
            UUID id,
            UUID merchantId,
            UUID merchantSettlementAccountId,
            String captureId,
            BigDecimal amount,
            String currency,
            String status,
            String createdAt,
            String updatedAt,
            long version
    ) {
        /**
         * Maps a domain MerchantPayout entity to a response DTO.
         */
        public static Response from(MerchantPayout p) {
            OffsetDateTime created = p.getCreatedAt();
            OffsetDateTime updated = p.getUpdatedAt();

            return new Response(
                    p.getId(),
                    p.getMerchantId(),
                    p.getMerchantSettlementAccountId(),
                    p.getCaptureId(),
                    p.getAmount(),
                    p.getCurrency(),
                    p.getStatus() != null ? p.getStatus().name() : null,
                    created != null ? created.toString() : null,
                    updated != null ? updated.toString() : null,
                    p.getVersion()
            );
        }
    }
}
