package com.hiba.booking.customer;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CustomerRequest(
        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "First name is required")
        String firstName,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email,

        @Min(value = 10000000, message = "Invalid phone number")
        @Max(value = 99999999, message = "Invalid phone number")
        int phone,

        boolean gender, // true = male, false = female

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotNull(message = "Address is required")
        Address address
) { }
