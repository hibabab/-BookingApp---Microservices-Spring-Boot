package com.hiba.booking.room;

import java.time.LocalDateTime;

public record RoomBookingRequest(
        Long roomId,
        LocalDateTime checkIn,
        LocalDateTime checkOut


) {}


