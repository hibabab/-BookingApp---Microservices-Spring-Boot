package booking.example.booking.booking;

public record BookingResponse(
        String reference,
        String customerId,
        int totalPrice,
        PaymentType paymentType

) {
}
