package com.sam.finflow.domain.disbursement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DisbursementRepository extends JpaRepository<Disbursement, UUID> {

    // Get all disbursements made by a customer
    List<Disbursement> findByCustomerId(UUID customerId);

    // Retrieve disbursement by idempotency key (used to prevent duplicate transfers)
    Optional<Disbursement> findByIdempotencyKey(String key);
}
