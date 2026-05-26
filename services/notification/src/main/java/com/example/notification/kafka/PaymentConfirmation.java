package com.example.notification.kafka;

import java.math.BigDecimal;

public record PaymentConfirmation(
        String Reference,
        int amount,
        String customerFirstname,
        String customerLastname,
        String customerEmail
) {
}