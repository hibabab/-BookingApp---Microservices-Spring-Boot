package com.hiba.booking.hotel;

import java.time.LocalDate;

public record RegulationDto(
        Long id,
        LocalDate checkInTime,
        LocalDate checkOutTime,
        Boolean refundable,
        String description
) {}
