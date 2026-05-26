package com.Bookingapp.payment.payment;


public record PaymentRequest(
        int amount,
        String reference,
        Customer customer
) {
}