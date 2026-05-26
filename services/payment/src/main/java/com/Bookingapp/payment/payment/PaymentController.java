package com.Bookingapp.payment.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/v1/payments")

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            Payment payment = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            // Si le paiement n'a pas été vérifié
            return ResponseEntity.badRequest().body(null);
        }
    }
}