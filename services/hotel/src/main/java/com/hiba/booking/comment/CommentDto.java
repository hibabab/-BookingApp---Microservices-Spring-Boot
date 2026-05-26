package com.hiba.booking.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CommentDto(
        @NotBlank(message = "Author first name is required")
        String firstName,

        @NotBlank(message = "Author last name is required")
        String lastName,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Author type is required")
        AuthorType authorType,

        LocalDate date
) {
}
