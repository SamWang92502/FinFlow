package com.sam.finflow.domain.banklink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class BankLinkService {
    private final BankLinkRepository repo;

    public BankLinkService(BankLinkRepository repo) {
        this.repo = repo;
    }

    //Checking if a customer already linked the same bank account
    @Transactional
    public BankLink createOrGet(UUID customerId,
                                String provider,
                                String providerAccountId,
                                String institutionName,
                                String last4) {
        if (repo.existsByCustomerIdAndProviderAndProviderAccountId(customerId, provider, providerAccountId)) {
            //stream: like a “conveyor belt” that passes each BankLink object one by one through some filters
            return repo.findByCustomerId(customerId).stream()
                    .filter(bl -> bl.getProvider().equals(provider)
                            && bl.getProviderAccountId().equals(providerAccountId))
                    .findFirst().orElseThrow();
        }
        BankLink bl = new BankLink(customerId, provider, providerAccountId, institutionName, last4);
        bl.activate(); // when provider confirms; otherwise keep as PENDING until verified
        return repo.save(bl);
    }

    @Transactional
    public void makePrimary(UUID customerId, UUID bankLinkId) {
        repo.clearPrimaryForCustomer(customerId);     // set existing primary -> false
        BankLink bl = repo.findById(bankLinkId).orElseThrow(); //findById: JPA built in method
        if (!bl.getCustomerId().equals(customerId)) throw new IllegalArgumentException("not owner");
        bl.makePrimary();                             // requires status ACTIVE
        repo.save(bl);                                // DB partial unique index guarantees only one true
    }
}
