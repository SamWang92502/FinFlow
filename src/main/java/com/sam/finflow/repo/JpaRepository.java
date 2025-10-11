package com.sam.finflow.repo;

import com.sam.finflow.domain.customer.Customer;
import com.sam.finflow.domain.banklink.BankLink;
import com.sam.finflow.domain.disbursement.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CustomerRepository extends JpaRepository<Customer, UUID> { }

public interface BankLinkRepository extends JpaRepository<BankLink, UUID> {
    List<BankLink> findByCustomerId(UUID customerId);
    Optional<BankLink> findByExternalAccountId(String externalAccountId);
}

public interface DisbursementRepository extends JpaRepository<Disbursement, UUID> {
    List<Disbursement> findByCustomerId(UUID customerId);
    Optional<Disbursement> findByIdempotencyKey(String key);
}
