package com.sam.finflow.domain.payout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantPayoutRepository extends JpaRepository<MerchantPayout, UUID> {

    Optional<MerchantPayout> findByMerchantIdAndCaptureId(UUID merchantId, String captureId);

    //Desc = “descending,” meaning the newest payout appears first.
    List<MerchantPayout> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
}
