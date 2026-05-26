package booking.example.booking.booking;

import booking.example.booking.room.RoomBookingRequest;

import java.util.List;

public record BookingRequest(
        String customerId,
        int totalPrice,
        PaymentType paymentType,
        List<RoomBookingRequest> bookingLines
) {}
