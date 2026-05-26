package com.hiba.booking.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<String> createCustomer(
            @RequestBody @Valid CustomerRequest request
    ) {
        String customerId = customerService.createCustomer(request);
        return ResponseEntity.ok(customerId);
    }

    @PutMapping
    public ResponseEntity<Void> updateCustomer(
            @PathVariable("customer-id") String customerId,
            @RequestBody @Valid CustomerRequest request
    ) {
        customerService.updateCustomer(request, customerId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAllCustomers() {
        List<CustomerResponse> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerResponse> findById(
            @PathVariable("customer-id") String customerId
    ) {
        CustomerResponse customer = customerService.findById(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/exists/{customer-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("customer-id") String customerId
    ) {
        boolean exists = customerService.existsById(customerId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable("customer-id") String customerId
    ) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}