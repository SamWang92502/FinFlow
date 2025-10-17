package com.sam.finflow.domain.banklink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankLinkRepository extends JpaRepository<BankLink, UUID> {

    List<BankLink> findByCustomerId(UUID customerId);

    List<BankLink> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    Optional<BankLink> findByCustomerIdAndProviderAndProviderAccountId(
            UUID customerId, String provider, String providerAccountId);

    Optional<BankLink> findByCustomerIdAndPrimaryTrue(UUID customerId);

    boolean existsByCustomerIdAndProviderAndProviderAccountId(UUID customerId, String provider, String providerAccountId);

    //make sure the system’s memory and database stay in sync
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE BankLink bl SET bl.primary = false WHERE bl.customerId = :customerId AND bl.primary = true")
    int clearPrimaryForCustomer(UUID customerId); //called when switching primary bank link
}
