package booking.example.booking.BookingLine;



public class BookingLineMapper {

    public static BookingLine toEntity(BookingLineRequest request) {
        return BookingLine.builder()
                .roomId(request.roomId())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .build();
    }

    public static BookingLineResponse toResponse(BookingLine bookingLine) {
        return new BookingLineResponse(
                bookingLine.getId(),
                bookingLine.getRoomId(),
                bookingLine.getCheckIn(),
                bookingLine.getCheckOut()
        );
    }
}
