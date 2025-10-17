package com.sam.finflow.controller;

import org.springframework.http.HttpStatus;
import com.sam.finflow.domain.customer.Customer;
import com.sam.finflow.domain.customer.CustomerService;
import com.sam.finflow.dto.CustomerDto.Response;
import com.sam.finflow.dto.CustomerDto.CreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customers;

    public CustomerController(CustomerService customers) {
        this.customers = customers;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRequest req) {
        try {
            Customer created = customers.createCustomer(req.email(), req.fullName());
            return ResponseEntity
                    .created(URI.create("/customers/" + created.getId()))
                    .body(Response.from(created));
        } catch (IllegalStateException e) {
            // email already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@PathVariable UUID id) {
        return customers.getCustomer(id)
                .map(c -> ResponseEntity.ok(Response.from(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
