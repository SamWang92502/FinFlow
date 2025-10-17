package com.sam.finflow.domain.banklink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.Nullable;     // <- for @Nullable
import java.time.OffsetDateTime;            // <- for OffsetDateTime
import java.util.UUID;
import java.util.List;

@Service
public class BankLinkService {
    private final BankLinkRepository repo;

    public record CreateOrGetResult(BankLink link, boolean created) {}

    public BankLinkService(BankLinkRepository repo) {
        this.repo = repo;
    }

    //Checking if a customer already linked the same bank account
    @Transactional
    public CreateOrGetResult createOrGet(UUID customerId,
                                         String provider,
                                         String providerAccountId,
                                         String institutionName,
                                         String last4) {
        try {
            BankLink bl = new BankLink(customerId, provider, providerAccountId, institutionName, last4);
            BankLink saved = repo.save(bl);
            return new CreateOrGetResult(saved, true);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // someone created it concurrently or it already existed
            BankLink existing = repo.findByCustomerIdAndProviderAndProviderAccountId(
                    customerId, provider, providerAccountId
            ).orElseThrow();
            return new CreateOrGetResult(existing, false);
        }
    }

    @Transactional
    public void makePrimary(UUID customerId, UUID bankLinkId) {
        repo.clearPrimaryForCustomer(customerId);     // set existing primary -> false
        BankLink bl = repo.findById(bankLinkId).orElseThrow(); //findById: JPA built in method
        if (!bl.getCustomerId().equals(customerId)) throw new IllegalArgumentException("not owner");
        bl.makePrimary();                             // requires status ACTIVE
        repo.save(bl);                                // DB partial unique index guarantees only one true
    }

    @Transactional(readOnly = true)
    public List<BankLink> listByCustomer(UUID customerId) {
        // Use the ordered version if you created it:
        return repo.findByCustomerIdOrderByCreatedAtDesc(customerId);
        // Or, if you chose the unordered finder:
        // return repo.findByCustomerId(customerId);
    }

    @Transactional
    public BankLink activate(UUID bankLinkId, @Nullable OffsetDateTime consentAt) {
        BankLink bl = repo.findById(bankLinkId).orElseThrow();
        bl.activate(consentAt);   // domain rules enforce PENDING -> ACTIVE
        return repo.save(bl);     // or rely on JPA dirty checking
    }

    @Transactional
    public BankLink revoke(UUID bankLinkId) {
        BankLink bl = repo.findById(bankLinkId).orElseThrow();
        bl.revoke();              // domain rules enforce idempotence
        return repo.save(bl);
    }

}
