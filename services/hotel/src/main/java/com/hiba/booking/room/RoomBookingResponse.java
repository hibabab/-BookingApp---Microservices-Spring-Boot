package com.hiba.booking.room;

import java.time.LocalDateTime;

public record RoomBookingResponse(
        Long roomId,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Integer price
) {
}
