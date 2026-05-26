package com.Bookingapp.payment.payment;

// Wrapper pour la réponse complète
public record PaymentResponse(
        Result result,
        String name,
        int code,
        String version
) {}

