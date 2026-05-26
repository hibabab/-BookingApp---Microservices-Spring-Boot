package booking.example.booking.BookingLine;

import java.time.LocalDateTime;

public record BookingLineRequest(Long roomId,
                                 LocalDateTime checkIn,
                                 LocalDateTime checkOut) {
}
