package booking.example.booking.BookingLine;

import java.time.LocalDateTime;

public record BookingLineResponse(Integer id,
                                  Long roomId,
                                  LocalDateTime checkIn,
                                  LocalDateTime checkOut) {
}
