package com.sam.finflow.controller;

import com.sam.finflow.dto.BankLinkDto.CreateBankLinkRequest;
import com.sam.finflow.dto.BankLinkDto.BankLinkResponse;
import com.sam.finflow.domain.banklink.BankLink;
import com.sam.finflow.domain.banklink.BankLinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class BankLinkController {

    private final BankLinkService bankLinks;

    public BankLinkController(BankLinkService bankLinks) {
        this.bankLinks = bankLinks;
    }


    // POST /bank-links
    @PostMapping("/bank-links")
    public ResponseEntity<BankLinkResponse> create(@Valid @RequestBody CreateBankLinkRequest req) {
        var res = bankLinks.createOrGet(req.customerId(), req.provider(), req.providerAccountId(),
                /* institutionName */ null, /* last4 */ null); // null -> MVP
        //BankLinkResponse.from(...) converts the domain object into a DTO (your public API shape).
        BankLinkResponse body = BankLinkResponse.from(res.link()); //res.link() → the domain entity BankLink
        if (res.created()) { //res.created() → a boolean telling you if a new row was created
            return ResponseEntity
                    .created(URI.create("/bank-links/" + res.link().getId()))
                    .body(body);                 // 201 Created
        } else {
            return ResponseEntity.ok(body);     // 200 OK (already existed)
        }
    }


    // GET /customers/{customerId}/bank-links
    @GetMapping("/customers/{customerId}/bank-links")
    public List<BankLinkResponse> listByCustomer(@PathVariable UUID customerId) {
        return bankLinks.listByCustomer(customerId).stream()
                .map(BankLinkResponse::from)
                .toList();
    }

    // PATCH /bank-links/{bankLinkId}/primary?customerId=...
    @PatchMapping("/bank-links/{bankLinkId}/primary")
    public ResponseEntity<List<BankLinkResponse>> makePrimary(
            @PathVariable UUID bankLinkId,
            @RequestParam UUID customerId
    ) {
        bankLinks.makePrimary(customerId, bankLinkId);
        var list = bankLinks.listByCustomer(customerId).stream().map(BankLinkResponse::from).toList();
        return ResponseEntity.ok(list);
    }

    // PATCH /bank-links/{bankLinkId}/activate?consentAt=2025-10-10T12:34:56Z
    @PatchMapping("/bank-links/{bankLinkId}/activate")
    public ResponseEntity<BankLinkResponse> activate(
            @PathVariable UUID bankLinkId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime consentAt
    ) {
        BankLink bl = bankLinks.activate(bankLinkId, consentAt); // service fills now() if null
        return ResponseEntity.ok(BankLinkResponse.from(bl));
    }

    // PATCH /bank-links/{bankLinkId}/revoke
    @PatchMapping("/bank-links/{bankLinkId}/revoke")
    public ResponseEntity<BankLinkResponse> revoke(@PathVariable UUID bankLinkId) {
        BankLink bl = bankLinks.revoke(bankLinkId);
        return ResponseEntity.ok(BankLinkResponse.from(bl));
    }
}
