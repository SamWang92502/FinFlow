package com.sam.finflow.dto;

import com.sam.finflow.domain.banklink.BankLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public final class BankLinkDto {

    private BankLinkDto() {} // prevent instantiation

    // --- Request DTO ---
    public record CreateBankLinkRequest(
            @NotNull UUID customerId,
            @NotBlank String provider,
            @NotBlank String providerAccountId
    ) {}

    // --- Response DTO ---
    public record BankLinkResponse(
            UUID id,
            UUID customerId,
            String provider,
            String providerAccountId,
            String status,
            Boolean activeMethod,
            String consentAt
    ) {
        public static BankLinkResponse from(BankLink bl) {
            return new BankLinkResponse(
                    bl.getId(),
                    bl.getCustomerId(),
                    bl.getProvider(),
                    bl.getProviderAccountId(),
                    bl.getStatus().name(),
                    bl.isPrimary(),
                    bl.getConsentAt() != null ? bl.getConsentAt().toString() : null
            );
        }
    }
}
