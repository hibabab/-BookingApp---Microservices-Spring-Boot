package com.Bookingapp.payment.payment;

import java.time.LocalDate;

public record Customer(
        String id,
        String firstName,
        String lastName,
        String email,
        int phone,
        boolean gender,
        LocalDate birthDate
) { }

