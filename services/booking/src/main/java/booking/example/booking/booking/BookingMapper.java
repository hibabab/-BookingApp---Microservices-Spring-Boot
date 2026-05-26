package booking.example.booking.booking;


public class BookingMapper {


    public static Booking toEntity(BookingRequest request) {
        return Booking.builder()
                .customerId(request.customerId())
                .totalPrice(request.totalPrice())
                .paymentType(request.paymentType())
                .status(BookingStatus.PENDING)
                .build();
    }


    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getReference(),
                booking.getCustomerId(),
                booking.getTotalPrice(),
                booking.getPaymentType()

        );
    }
}
