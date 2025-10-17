package com.sam.finflow.dto;

import com.sam.finflow.domain.customer.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class CustomerDto {
    private CustomerDto() {} // private: prevent other call new CustomerDto()

    // --- Request DTO ---
    public record CreateRequest(
            @NotBlank @Email String email,
            @NotBlank String fullName
    ) {}

    // --- Response DTO ---
    public record Response(
            UUID id,
            String fullName,
            String email,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        public static Response from(Customer c) {
            return new Response(
                    c.getId(),
                    c.getFullName(),
                    c.getEmail(),
                    c.getCreatedAt(),
                    c.getUpdatedAt()
            );
        }
    }
}
