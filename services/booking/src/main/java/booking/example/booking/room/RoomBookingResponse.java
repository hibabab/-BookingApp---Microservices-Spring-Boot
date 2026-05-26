package booking.example.booking.room;

import java.time.LocalDateTime;



import java.time.LocalDateTime;

public record RoomBookingResponse(
        Long roomId,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Integer price
) {
}
