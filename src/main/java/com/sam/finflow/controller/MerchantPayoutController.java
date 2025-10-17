package com.sam.finflow.controller;

import com.sam.finflow.domain.payout.MerchantPayout;
import com.sam.finflow.domain.payout.MerchantPayoutService;
import com.sam.finflow.dto.MerchantPayoutDto.Response;
import com.sam.finflow.dto.MerchantPayoutDto.CreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Minimal REST controller. Uses small request/response records to avoid extra DTO files.
 */
@RestController
@RequestMapping("/merchant-payouts")
public class MerchantPayoutController {

    private final MerchantPayoutService service;

    public MerchantPayoutController(MerchantPayoutService service) {
        this.service = service;
    }

    // --- Create (idempotent by merchantId+captureId) ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@Valid @RequestBody CreateRequest req) {
        MerchantPayout p = service.createOrGet(
                req.merchantId(),
                req.merchantSettlementAccountId(),
                req.captureId(),
                req.amount(),
                req.currency()
        );
        return Response.from(p);
    }

    // --- Read ---
    @GetMapping("/{id}")
    public Response get(@PathVariable UUID id) {
        return Response.from(service.getOrThrow(id));
    }

    // --- List by merchant ---
    @GetMapping("/merchant/{merchantId}")
    public List<Response> listByMerchant(@PathVariable UUID merchantId) {
        return service.listForMerchant(merchantId).stream().map(Response::from).toList();
    }

    // --- State transitions (simple POST endpoints) ---
    @PatchMapping("/{id}/processing")
    public Response markProcessing(@PathVariable UUID id) {
        return Response.from(service.markProcessing(id));
    }

    @PatchMapping("/{id}/settled")
    public Response markSettled(@PathVariable UUID id) {
        return Response.from(service.markSettled(id));
    }

    @PatchMapping("/{id}/failed")
    public Response markFailed(@PathVariable UUID id) {
        return Response.from(service.markFailed(id));
    }
}
