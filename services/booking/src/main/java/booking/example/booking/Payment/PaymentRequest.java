package booking.example.booking.Payment;

import booking.example.booking.customer.CustomerResponse;

public record PaymentRequest(
        int amount,
        String reference,
        CustomerResponse customer
) {
}