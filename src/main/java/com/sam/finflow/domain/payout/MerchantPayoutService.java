package com.sam.finflow.domain.payout;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MerchantPayoutService {

    private final MerchantPayoutRepository repo;

    public MerchantPayoutService(MerchantPayoutRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public MerchantPayout createOrGet(UUID merchantId,
                                      UUID merchantSettlementAccountId,
                                      String captureId,
                                      BigDecimal amount,
                                      String currency) {
        // Idempotency by (merchantId, captureId)
        // Try to find an existing payout for this merchant + capture.
        // If it exists, just give it to me.
        // If not, create a new one and save it.
        return repo.findByMerchantIdAndCaptureId(merchantId, captureId)
                .orElseGet(() -> repo.save(
                        new MerchantPayout(merchantId, merchantSettlementAccountId, captureId, amount, currency)
                ));
    }

    @Transactional
    public MerchantPayout markProcessing(UUID payoutId) {
        MerchantPayout p = getOrThrow(payoutId);
        p.markProcessing();
        return p;
    }

    @Transactional
    public MerchantPayout markSettled(UUID payoutId) {
        MerchantPayout p = getOrThrow(payoutId);
        p.markSettled();
        return p;
    }

    @Transactional
    public MerchantPayout markFailed(UUID payoutId) {
        MerchantPayout p = getOrThrow(payoutId);
        p.markFailed();
        return p;
    }

    //id == payoutId
    @Transactional
    public MerchantPayout getOrThrow(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("payout not found: " + id));
    }

    @Transactional
    public List<MerchantPayout> listForMerchant(UUID merchantId) {
        return repo.findByMerchantIdOrderByCreatedAtDesc(merchantId);
    }
}
