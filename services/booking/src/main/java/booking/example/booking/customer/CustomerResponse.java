package booking.example.booking.customer;



import java.time.LocalDate;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        int phone,
        boolean gender,
        LocalDate birthDate
) { }
